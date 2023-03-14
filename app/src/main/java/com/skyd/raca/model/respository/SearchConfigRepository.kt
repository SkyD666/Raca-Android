package com.skyd.raca.model.respository

import com.skyd.raca.base.BaseData
import com.skyd.raca.base.BaseRepository
import com.skyd.raca.db.appDataBase
import com.skyd.raca.model.bean.SearchDomainBean
import javax.inject.Inject

class SearchConfigRepository @Inject constructor() : BaseRepository() {
    suspend fun requestGetSearchDomain(): BaseData<Map<String, Boolean>> {
        return executeRequest {
            val map = mutableMapOf<String, Boolean>()
            appDataBase.searchDomainDao().getAllSearchDomain().forEach {
                map["${it.tableName}/${it.columnName}"] = it.search
            }
            BaseData<Map<String, Boolean>>().apply {
                code = 0
                data = map
            }
        }
    }

    suspend fun requestSetSearchDomain(searchDomainBean: SearchDomainBean): BaseData<Map<String, Boolean>> {
        return executeRequest {
            appDataBase.searchDomainDao().setSearchDomain(searchDomainBean)
            val map = mutableMapOf<String, Boolean>()
            appDataBase.searchDomainDao().getAllSearchDomain().forEach {
                map["${it.tableName}/${it.columnName}"] = it.search
            }
            BaseData<Map<String, Boolean>>().apply {
                code = 0
                data = map
            }
        }
    }
}