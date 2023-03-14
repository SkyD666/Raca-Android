package com.skyd.raca.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.skyd.raca.model.bean.*
import com.skyd.raca.model.bean.SearchDomainBean.Companion.COLUMN_NAME_COLUMN
import com.skyd.raca.model.bean.SearchDomainBean.Companion.SEARCH_COLUMN
import com.skyd.raca.model.bean.SearchDomainBean.Companion.TABLE_NAME_COLUMN

@Dao
interface SearchDomainDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun setSearchDomain(searchDomainBean: SearchDomainBean)

    @Query(
        value = "SELECT $SEARCH_COLUMN FROM $SEARCH_DOMAIN_TABLE_NAME" +
                " WHERE $TABLE_NAME_COLUMN LIKE :tableName AND $COLUMN_NAME_COLUMN LIKE :columnName"
    )
    fun getSearchDomainOrNull(tableName: String, columnName: String): Boolean?

    fun getSearchDomain(tableName: String, columnName: String): Boolean {
        return getSearchDomainOrNull(tableName, columnName)
            ?: if (tableName == ARTICLE_TABLE_NAME &&
                (columnName == ArticleBean.TITLE_COLUMN ||
                        columnName == ArticleBean.ARTICLE_COLUMN)
            ) true else tableName == TAG_TABLE_NAME && columnName == TagBean.TAG_COLUMN
    }

    @Query(value = "SELECT * FROM $SEARCH_DOMAIN_TABLE_NAME")
    fun getAllSearchDomain(): List<SearchDomainBean>
}