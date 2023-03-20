package com.skyd.raca.ui.service

import android.app.assist.AssistStructure
import android.os.Build
import android.os.CancellationSignal
import android.service.autofill.*
import android.view.autofill.AutofillId
import android.view.autofill.AutofillValue
import android.widget.RemoteViews
import androidx.annotation.RequiresApi
import com.skyd.raca.R
import com.skyd.raca.db.dao.ArticleDao
import com.skyd.raca.model.bean.ArticleWithTags
import com.skyd.raca.model.respository.HomeRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject


@RequiresApi(Build.VERSION_CODES.O)
@AndroidEntryPoint
class RacaAutofillService : AutofillService() {
    @Inject
    lateinit var articleDao: ArticleDao
    private val scope = CoroutineScope(Dispatchers.IO)

    override fun onFillRequest(
        request: FillRequest,
        cancellationSignal: CancellationSignal,
        callback: FillCallback
    ) {
        val structure = request.fillContexts.lastOrNull()?.structure
            ?: run {
                callback.onSuccess(null)
                return
            }
        val viewNode = traverseStructure(structure) ?: run {
            callback.onSuccess(null)
            return
        }
        val autofillId = viewNode.autofillId ?: run {
            callback.onSuccess(null)
            return
        }

        scope.launch {
            val articleWithTagsList = articleDao.getArticleWithTagsList(
                HomeRepository.genSql(viewNode.autofillValue?.textValue.toString())
            )
            withContext(Dispatchers.Main) {
                if (articleWithTagsList.isEmpty()) {
                    callback.onSuccess(null)
                } else {
                    val fillResponse: FillResponse = FillResponse.Builder()
                        .setData(autofillId, articleWithTagsList)
                        .build()
                    callback.onSuccess(fillResponse)
                }
            }
        }

    }

    private fun FillResponse.Builder.setData(
        autofillId: AutofillId,
        articleWithTagsList: List<ArticleWithTags>,
        textValue: String = ""
    ): FillResponse.Builder {
        articleWithTagsList.forEach {
            val presentation = RemoteViews(packageName, R.layout.auto_fill_menu_item_1)
            presentation.setTextViewText(R.id.auto_fill_menu_item_1_article, it.article.article)
            val prefix = if (textValue.isEmpty()) "" else "$textValue\n\n"
            addDataset(
                Dataset.Builder()
//                    .setField(autofillId, Field.Builder()
//                        .setPresentations(Presentations.Builder()
//                            .setMenuPresentation(presentation)
//                            .build())
//                        .setValue(AutofillValue.forText(textValue + it.article.article))
//                        .build())
                    .setValue(
                        autofillId,
                        AutofillValue.forText(prefix + it.article.article),
                        presentation
                    )
                    .build()
            )
        }
        return this
    }

    private fun traverseStructure(structure: AssistStructure): AssistStructure.ViewNode? {
        val windowNodes: List<AssistStructure.WindowNode> =
            structure.run {
                (0 until windowNodeCount).map { getWindowNodeAt(it) }
            }

        windowNodes.forEach { windowNode: AssistStructure.WindowNode ->
            val viewNode: AssistStructure.ViewNode? = windowNode.rootViewNode
            val vn = traverseNode(viewNode)
            if (vn != null) return vn
        }
        return null
    }

    private fun traverseNode(viewNode: AssistStructure.ViewNode?): AssistStructure.ViewNode? {
        if (viewNode?.isFocused == true) {
            return viewNode
        }

        val children: List<AssistStructure.ViewNode>? =
            viewNode?.run {
                (0 until childCount).map { getChildAt(it) }
            }

        children?.forEach { childNode: AssistStructure.ViewNode ->
            val vn = traverseNode(childNode)
            if (vn != null) return vn
        }
        return null
    }

    override fun onSaveRequest(request: SaveRequest, callback: SaveCallback) {

    }

}