package com.skyd.raca.model.respository

import android.database.DatabaseUtils
import androidx.sqlite.db.SimpleSQLiteQuery
import com.skyd.raca.appContext
import com.skyd.raca.base.BaseData
import com.skyd.raca.base.BaseRepository
import com.skyd.raca.config.allSearchDomain
import com.skyd.raca.db.dao.ArticleDao
import com.skyd.raca.db.dao.SearchDomainDao
import com.skyd.raca.ext.dataStore
import com.skyd.raca.ext.get
import com.skyd.raca.model.bean.ARTICLE_TABLE_NAME
import com.skyd.raca.model.bean.ArticleBean
import com.skyd.raca.model.bean.ArticleWithTags
import com.skyd.raca.model.bean.TagBean
import com.skyd.raca.model.preference.IntersectSearchBySpacePreference
import com.skyd.raca.model.preference.UseRegexSearchPreference
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class HomeRepository @Inject constructor(private val articleDao: ArticleDao) : BaseRepository() {
    suspend fun requestArticleWithTagsList(keyword: String): Flow<BaseData<List<ArticleWithTags>>> {
        return flow {
            emitBaseData(BaseData<List<ArticleWithTags>>().apply {
                code = 0
                data = articleDao.getArticleWithTagsList(genSql(keyword))
            })
        }
    }

    suspend fun requestArticleWithTagsDetail(articleUuid: String): Flow<BaseData<ArticleWithTags>> {
        return flow {
            val articleWithTags = articleDao.getArticleWithTags(articleUuid)
            emitBaseData(BaseData<ArticleWithTags>().apply {
                code = if (articleWithTags == null) 1 else 0
                data = articleWithTags
            })
        }
    }

    suspend fun requestDeleteArticleWithTagsDetail(articleUuid: String): Flow<BaseData<Int>> {
        return flow {
            emitBaseData(BaseData<Int>().apply {
                code = 0
                data = articleDao.deleteArticleWithTags(articleUuid)
            })
        }
    }

    companion object {
        @EntryPoint
        @InstallIn(SingletonComponent::class)
        interface HomeRepositoryEntryPoint {
            val searchDomainDao: SearchDomainDao
        }

        fun genSql(k: String): SimpleSQLiteQuery {
            val hiltEntryPoint = EntryPointAccessors.fromApplication(
                appContext, HomeRepositoryEntryPoint::class.java
            )
            // 是否使用多个关键字并集查询
            val intersectSearchBySpace =
                appContext.dataStore.get(IntersectSearchBySpacePreference.key) ?: true
            return if (intersectSearchBySpace) {
                // 以多个连续的空格/制表符/换行符分割
                val keywords = k.trim().split("\\s+".toRegex()).toSet()
                val sql = buildString {
                    keywords.forEachIndexed { index, s ->
                        if (index > 0) append("INTERSECT \n")
                        append(
                            "SELECT * FROM $ARTICLE_TABLE_NAME WHERE ${
                                getFilter(s, hiltEntryPoint.searchDomainDao)
                            } \n"
                        )
                    }
                }
                SimpleSQLiteQuery(sql)
            } else {
                SimpleSQLiteQuery(
                    "SELECT * FROM $ARTICLE_TABLE_NAME WHERE ${
                        getFilter(k, hiltEntryPoint.searchDomainDao)
                    }"
                )
            }
        }

        private fun getFilter(k: String, searchDomainDao: SearchDomainDao): String {
            if (k.isBlank()) return "1"

            val useRegexSearch = appContext.dataStore.get(UseRegexSearchPreference.key) ?: false

            var filter = "0"

            // 转义输入，防止SQL注入
            val keyword = if (useRegexSearch) {
                // 检查正则表达式是否有效
                runCatching { k.toRegex() }.onFailure { error(it.message.orEmpty()) }
                DatabaseUtils.sqlEscapeString(k)
            } else {
                DatabaseUtils.sqlEscapeString("%$k%")
            }

            val tables = allSearchDomain.keys
            for (table in tables) {
                val columns = allSearchDomain[table].orEmpty()

                if (table.first == ARTICLE_TABLE_NAME) {
                    for (column in columns) {
                        if (!searchDomainDao.getSearchDomain(table.first, column.first)) {
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
                        if (!searchDomainDao.getSearchDomain(table.first, column.first)) {
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