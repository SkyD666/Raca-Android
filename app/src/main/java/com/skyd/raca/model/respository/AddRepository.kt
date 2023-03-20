package com.skyd.raca.model.respository

import com.skyd.raca.base.BaseData
import com.skyd.raca.base.BaseRepository
import com.skyd.raca.db.dao.ArticleDao
import com.skyd.raca.model.bean.ArticleWithTags
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class AddRepository @Inject constructor(private val articleDao: ArticleDao) : BaseRepository() {
    suspend fun requestAddArticleWithTags(articleWithTags: ArticleWithTags): Flow<BaseData<String>> {
        return flow {
            emitBaseData(BaseData<String>().apply {
                code = 0
                data = articleDao.addArticleWithTags(articleWithTags)
            })
        }
    }

    suspend fun requestGetArticleWithTags(articleUuid: String): Flow<BaseData<ArticleWithTags>> {
        return flow {
            emitBaseData(BaseData<ArticleWithTags>().apply {
                code = 0
                data = articleDao.getArticleWithTags(articleUuid)
            })
        }
    }
}