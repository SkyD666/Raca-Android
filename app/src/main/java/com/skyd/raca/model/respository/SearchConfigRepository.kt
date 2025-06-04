package com.skyd.raca.model.respository

import com.skyd.raca.base.BaseData
import com.skyd.raca.base.BaseRepository
import com.skyd.raca.db.dao.SearchDomainDao
import com.skyd.raca.model.bean.SearchDomainBean
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class SearchConfigRepository(
    private val searchDomainDao: SearchDomainDao
) : BaseRepository() {
    fun requestGetSearchDomain(): Flow<BaseData<Map<String, Boolean>>> = flow {
        val map = mutableMapOf<String, Boolean>()
        searchDomainDao.getAllSearchDomain().forEach {
            map["${it.tableName}/${it.columnName}"] = it.search
        }
        emitBaseData(BaseData<Map<String, Boolean>>().apply {
            code = 0
            data = map
        })
    }

    fun requestSetSearchDomain(
        searchDomainBean: SearchDomainBean
    ): Flow<BaseData<Map<String, Boolean>>> = flow {
        searchDomainDao.setSearchDomain(searchDomainBean)
        val map = mutableMapOf<String, Boolean>()
        searchDomainDao.getAllSearchDomain().forEach {
            map["${it.tableName}/${it.columnName}"] = it.search
        }
        emitBaseData(BaseData<Map<String, Boolean>>().apply {
            code = 0
            data = map
        })
    }
}