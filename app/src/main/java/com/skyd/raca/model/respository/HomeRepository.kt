package com.skyd.raca.model.respository

import android.database.DatabaseUtils
import androidx.sqlite.db.SimpleSQLiteQuery
import com.skyd.raca.base.BaseData
import com.skyd.raca.base.BaseRepository
import com.skyd.raca.config.allSearchDomain
import com.skyd.raca.config.getSearchDomain
import com.skyd.raca.config.useRegexSearch
import com.skyd.raca.db.appDataBase
import com.skyd.raca.model.bean.ARTICLE_TABLE_NAME
import com.skyd.raca.model.bean.ArticleBean
import com.skyd.raca.model.bean.ArticleWithTags
import com.skyd.raca.model.bean.TagBean
import javax.inject.Inject

class HomeRepository @Inject constructor() : BaseRepository() {
    suspend fun requestArticleWithTagsList(keyword: String): BaseData<List<ArticleWithTags>> {
        return executeRequest {
            BaseData<List<ArticleWithTags>>().apply {
                code = 0
                data = appDataBase.articleDao().getArticleWithTagsList(genSql(keyword))
            }
        }
    }

    suspend fun requestArticleWithTagsDetail(articleUuid: String): BaseData<ArticleWithTags> {
        return executeRequest {
            BaseData<ArticleWithTags>().apply {
                code = 0
                data = appDataBase.articleDao().getArticleWithTags(articleUuid)
            }
        }
    }

    suspend fun requestDeleteArticleWithTagsDetail(articleUuid: String): BaseData<Int> {
        return executeRequest {
            BaseData<Int>().apply {
                code = 0
                data = appDataBase.articleDao().deleteArticleWithTags(articleUuid)
            }
        }
    }

    companion object {
        fun genSql(k: String): SimpleSQLiteQuery {
            return SimpleSQLiteQuery("SELECT * FROM $ARTICLE_TABLE_NAME WHERE ${getFilter(k)}")
        }

        private fun getFilter(k: String): String {
            if (k.isBlank()) return "1"

            var filter = "0"

            // 转义输入，防止SQL注入
            val keyword = if (useRegexSearch) {
                DatabaseUtils.sqlEscapeString(k)
            } else {
                DatabaseUtils.sqlEscapeString("%$k%")
            }

            val tables = allSearchDomain.keys
            for (table in tables) {
                val columns = allSearchDomain[table].orEmpty()

                if (table.first == ARTICLE_TABLE_NAME) {
                    for (column in columns) {
                        if (!getSearchDomain(table.first, column.first)) {
                            continue
                        }
                        filter += if (useRegexSearch) {
                            " OR ${column.first} REGEXP $keyword"
                        } else {
                            " OR ${column.first} LIKE $keyword"
                        }
                    }
                } else {
                    var hasQuery = false
                    var subSelect =
                        "(SELECT DISTINCT ${TagBean.ARTICLE_UUID_COLUMN} FROM ${table.first} WHERE 0 "
                    for (column in columns) {
                        if (!getSearchDomain(table.first, column.first)) {
                            continue
                        }
                        subSelect += if (useRegexSearch) {
                            " OR ${column.first} REGEXP $keyword"
                        } else {
                            " OR ${column.first} LIKE $keyword"
                        }
                        hasQuery = true
                    }
                    if (!hasQuery) {
                        continue
                    }
                    subSelect += ")"
                    filter += " OR ${ArticleBean.UUID_COLUMN} IN $subSelect"
                }
            }

            if (filter == "0") {
                filter += " OR 1"
            }
            return filter
        }
    }
}