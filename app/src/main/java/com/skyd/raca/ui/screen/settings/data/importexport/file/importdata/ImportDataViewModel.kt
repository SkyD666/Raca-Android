package com.skyd.raca.ui.screen.settings.data.importexport.file.importdata

import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import com.skyd.raca.appContext
import com.skyd.raca.base.BaseViewModel
import com.skyd.raca.base.IUIChange
import com.skyd.raca.base.IUiState
import com.skyd.raca.model.bean.ArticleBean
import com.skyd.raca.model.bean.ArticleWithTags
import com.skyd.raca.model.bean.TagBean
import com.skyd.raca.model.respository.ImportDataRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.merge
import java.io.InputStream
import java.util.UUID

class ImportDataViewModel(private var importDataRepo: ImportDataRepository) :
    BaseViewModel<IUiState, ImportDataEvent, ImportDataIntent>() {
    override fun initUiState(): IUiState {
        return object : IUiState {}
    }

    override fun IUIChange.checkStateOrEvent() = this as? IUiState to this as? ImportDataEvent

    override fun Flow<ImportDataIntent>.handleIntent(): Flow<IUIChange> = merge(
        doIsInstance<ImportDataIntent.StartImport> { intent ->
            val contentResolver = appContext.contentResolver
            val articleInputStream = contentResolver.openInputStream(intent.articleUri)
            val tagInputStream = contentResolver.openInputStream(intent.tagUri)
            importDataRepo.requestImportData(
                readArticleTagCsv(articleInputStream, tagInputStream)
            ).apply {
                articleInputStream?.close()
                tagInputStream?.close()
            }
                .mapToUIChange { data ->
                    ImportDataEvent(importResultUiEvent = ImportResultUiEvent.Success(data))
                }
                .defaultFinally()
        },
    )

    private fun readArticleTagCsv(
        articleInputStream: InputStream?,
        tagInputStream: InputStream?
    ): List<ArticleWithTags>? {
        if (articleInputStream == null || tagInputStream == null) return null

        val articleWithTagsList = mutableListOf<ArticleWithTags>()

        val tags = mutableMapOf<String, MutableList<TagBean>>()

        val articleInputs: List<Map<String, String>> =
            csvReader().readAllWithHeader(articleInputStream)
        val tagInputs: List<Map<String, String>> = csvReader().readAllWithHeader(tagInputStream)

        tagInputs.forEach {
            val articleUuid = it["articleUuid"] ?: return@forEach
            val tag = it["tag"] ?: return@forEach
            val createTime = it["createTime"]?.toLongOrNull() ?: return@forEach

            if (tags[articleUuid] == null) {
                tags[articleUuid] = mutableListOf()
            }
            tags[articleUuid]?.add(
                TagBean(
                    articleUuid = articleUuid,
                    tag = tag,
                    createTime = createTime
                )
            )
        }
        articleInputs.forEach {
            val title: String = it["title"] ?: return@forEach
            val article: String = it["article"] ?: return@forEach
            val createTime: Long = it["createTime"]?.toLongOrNull() ?: return@forEach
            val uuid: String = it["uuid"] ?: return@forEach
            runCatching {
                UUID.fromString(uuid)
            }.onFailure {
                return@forEach
            }

            val articleBean = ArticleBean(
                uuid = uuid,
                title = title,
                article = article,
                createTime = createTime,
            )

            articleWithTagsList.add(
                ArticleWithTags(
                    article = articleBean,
                    tags = tags[uuid].orEmpty()
                )
            )
        }

        return articleWithTagsList
    }
}