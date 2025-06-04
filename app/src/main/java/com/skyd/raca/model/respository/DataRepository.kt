package com.skyd.raca.model.respository

import com.skyd.raca.base.BaseData
import com.skyd.raca.base.BaseRepository
import com.skyd.raca.db.dao.ArticleDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class DataRepository(private val articleDao: ArticleDao) : BaseRepository() {
    fun requestDeleteAllData(): Flow<BaseData<Long>> = flow {
        val startTime = System.currentTimeMillis()
        articleDao.deleteAllArticleWithTags()
        emitBaseData(BaseData<Long>().apply {
            code = 0
            data = System.currentTimeMillis() - startTime
        })
    }
}