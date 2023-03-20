package com.skyd.raca.model.respository

import android.net.Uri
import androidx.documentfile.provider.DocumentFile
import com.github.doyaaaaaken.kotlincsv.dsl.csvWriter
import com.skyd.raca.appContext
import com.skyd.raca.base.BaseData
import com.skyd.raca.base.BaseRepository
import com.skyd.raca.db.dao.ArticleDao
import com.skyd.raca.db.dao.TagDao
import com.skyd.raca.model.bean.ARTICLE_TABLE_NAME
import com.skyd.raca.model.bean.ArticleBean
import com.skyd.raca.model.bean.TAG_TABLE_NAME
import com.skyd.raca.model.bean.TagBean
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class ExportDataRepository @Inject constructor(
    private val articleDao: ArticleDao,
    private val tagDao: TagDao,
) : BaseRepository() {
    suspend fun requestExportData(dirUri: Uri): Flow<BaseData<Long>> {
        return flow {
            val startTime = System.currentTimeMillis()
            val articleList = articleDao.getArticleList()
            val tagList = tagDao.getTagList()
            val (success, msg) = export(dirUri, articleList, tagList)
            emitBaseData(BaseData<Long>().apply {
                code = if (success) 0 else -1
                data = System.currentTimeMillis() - startTime
                this.msg = msg
            })
        }
    }

    private fun export(
        dirUri: Uri,
        articleList: List<ArticleBean>,
        tagList: List<TagBean>
    ): Pair<Boolean, String?> {
        val documentFile = DocumentFile.fromTreeUri(appContext, dirUri)
            ?: return false to "DocumentFile is null"
        val articleUri: Uri = documentFile.createFile(
            "text/csv",
            "${ARTICLE_TABLE_NAME}_${System.currentTimeMillis()}"
        )?.uri ?: return false to "Article uri is null"
        val tagUri: Uri = documentFile.createFile(
            "text/csv",
            "${TAG_TABLE_NAME}_${System.currentTimeMillis()}"
        )?.uri ?: return false to "Tag uri is null"
        val articleOutputStream =
            appContext.contentResolver.openOutputStream(articleUri)
                ?: return false to "Article InputStream is null"
        val tagOutputStream =
            appContext.contentResolver.openOutputStream(tagUri)
                ?: return false to "Tag InputStream is null"

        csvWriter().writeAll(
            mutableListOf(ArticleBean.columnName) + articleList.map { it.fields() },
            articleOutputStream
        )
        csvWriter().writeAll(
            mutableListOf(TagBean.columnName) + tagList.map { it.fields() },
            tagOutputStream
        )

        tagOutputStream.close()
        articleOutputStream.close()
        return true to null
    }
}