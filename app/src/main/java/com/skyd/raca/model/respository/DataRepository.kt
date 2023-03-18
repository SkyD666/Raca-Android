package com.skyd.raca.model.respository

import com.skyd.raca.base.BaseData
import com.skyd.raca.base.BaseRepository
import com.skyd.raca.db.appDataBase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class DataRepository @Inject constructor() : BaseRepository() {
    suspend fun requestDeleteAllData(): Flow<BaseData<Long>> {
        return flow {
            val startTime = System.currentTimeMillis()
            appDataBase.articleDao().deleteAllArticleWithTags()
            emitBaseData(BaseData<Long>().apply {
                code = 0
                data = System.currentTimeMillis() - startTime
            })
        }
    }
}