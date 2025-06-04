package com.skyd.raca.model.respository

import com.skyd.raca.base.BaseData
import com.skyd.raca.base.BaseRepository
import com.skyd.raca.db.dao.ArticleDao
import com.skyd.raca.model.bean.ArticleWithTags
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class AddRepository(private val articleDao: ArticleDao) : BaseRepository() {
    fun requestAddArticleWithTags(articleWithTags: ArticleWithTags): Flow<BaseData<String>> = flow {
        val containsByArticle = articleDao.containsByArticle(articleWithTags.article.article)
        if (containsByArticle != null && articleDao.containsByUuid(articleWithTags.article.uuid) == 0) {
            emitBaseData(BaseData<String>().apply {
                code = -2
                msg = "Duplicate article!"
                data = containsByArticle
            })
        } else {
            emitBaseData(BaseData<String>().apply {
                code = 0
                data = articleDao.addArticleWithTags(articleWithTags)
            })
        }
    }

    fun requestGetArticleWithTags(articleUuid: String): Flow<BaseData<ArticleWithTags>> = flow {
        emitBaseData(BaseData<ArticleWithTags>().apply {
            code = 0
            data = articleDao.getArticleWithTags(articleUuid)
        })
    }
}