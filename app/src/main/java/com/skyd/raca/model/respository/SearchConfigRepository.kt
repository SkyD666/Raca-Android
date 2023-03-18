package com.skyd.raca.model.respository

import com.skyd.raca.base.BaseData
import com.skyd.raca.base.BaseRepository
import com.skyd.raca.db.appDataBase
import com.skyd.raca.model.bean.SearchDomainBean
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class SearchConfigRepository @Inject constructor() : BaseRepository() {
    suspend fun requestGetSearchDomain(): Flow<BaseData<Map<String, Boolean>>> {
        return flow {
            val map = mutableMapOf<String, Boolean>()
            appDataBase.searchDomainDao().getAllSearchDomain().forEach {
                map["${it.tableName}/${it.columnName}"] = it.search
            }
            emitBaseData(BaseData<Map<String, Boolean>>().apply {
                code = 0
                data = map
            })
        }
    }

    suspend fun requestSetSearchDomain(
        searchDomainBean: SearchDomainBean
    ): Flow<BaseData<Map<String, Boolean>>> {
        return flow {
            appDataBase.searchDomainDao().setSearchDomain(searchDomainBean)
            val map = mutableMapOf<String, Boolean>()
            appDataBase.searchDomainDao().getAllSearchDomain().forEach {
                map["${it.tableName}/${it.columnName}"] = it.search
            }
            emitBaseData(BaseData<Map<String, Boolean>>().apply {
                code = 0
                data = map
            })
        }
    }
}