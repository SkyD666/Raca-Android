package com.skyd.raca.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.RawQuery
import androidx.room.Transaction
import androidx.sqlite.db.SupportSQLiteQuery
import com.skyd.raca.appContext
import com.skyd.raca.ext.dataStore
import com.skyd.raca.ext.get
import com.skyd.raca.model.bean.ARTICLE_TABLE_NAME
import com.skyd.raca.model.bean.ArticleBean
import com.skyd.raca.model.bean.ArticleBean.Companion.ARTICLE_COLUMN
import com.skyd.raca.model.bean.ArticleBean.Companion.UUID_COLUMN
import com.skyd.raca.model.bean.ArticleWithTags
import com.skyd.raca.model.preference.CurrentArticleUuidPreference
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import java.util.UUID

@Dao
interface ArticleDao {
    @Transaction
    @RawQuery
    fun getArticleWithTagsList(sql: SupportSQLiteQuery): List<ArticleWithTags>

    @Transaction
    @Query("SELECT * FROM $ARTICLE_TABLE_NAME")
    fun getAllArticleWithTagsList(): List<ArticleWithTags>

    @Transaction
    @Query("SELECT * FROM $ARTICLE_TABLE_NAME")
    fun getArticleList(): List<ArticleBean>

    @Transaction
    @Query("SELECT * FROM $ARTICLE_TABLE_NAME WHERE $UUID_COLUMN LIKE :articleUuid")
    fun getArticleWithTags(articleUuid: String): ArticleWithTags?

    @Transaction
    @Query("SELECT * FROM $ARTICLE_TABLE_NAME WHERE $ARTICLE_COLUMN LIKE :article")
    fun getArticleWithTagsList(article: String): List<ArticleWithTags>

    @Transaction
    @Query("SELECT COUNT(*) FROM $ARTICLE_TABLE_NAME WHERE $UUID_COLUMN LIKE :uuid")
    fun containsByUuid(uuid: String): Int

    @Transaction
    @Query("SELECT $UUID_COLUMN FROM $ARTICLE_TABLE_NAME WHERE $ARTICLE_COLUMN LIKE :article")
    fun containsByArticle(article: String): String?

    @Transaction
    fun addArticleWithTags(articleWithTags: ArticleWithTags): String {
        var articleUuid = articleWithTags.article.uuid
        runCatching {
            UUID.fromString(articleUuid)
        }.onFailure {
            articleUuid = UUID.randomUUID().toString()
            articleWithTags.article.uuid = articleUuid
        }
        innerAddArticle(articleWithTags.article)
        articleWithTags.tags.forEach {
            it.articleUuid = articleUuid
        }
        com.skyd.raca.di.get<TagDao>().apply {
            deleteTags(articleUuid)
            addTags(articleWithTags.tags)
        }
        return articleUuid
    }

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun innerAddArticle(article: ArticleBean)

    @Transaction
    fun deleteArticleWithTags(articleUuid: String): Int {
        val scope = CoroutineScope(Dispatchers.IO)
        val currentArticleUuid = appContext.dataStore.get(CurrentArticleUuidPreference.key)
        if (currentArticleUuid == articleUuid) {
            CurrentArticleUuidPreference.put(
                appContext, scope, CurrentArticleUuidPreference.default
            )
        }
        // 设置了外键，ForeignKey.CASCADE，因此会自动deleteTags
        return innerDeleteArticle(articleUuid)
    }

    @Transaction
    fun deleteAllArticleWithTags() {
        val scope = CoroutineScope(Dispatchers.IO)
        CurrentArticleUuidPreference.put(appContext, scope, CurrentArticleUuidPreference.default)
        // 设置了外键，ForeignKey.CASCADE，因此会自动deleteTags
        deleteAllArticles()
    }

    @Transaction
    @Query("DELETE FROM $ARTICLE_TABLE_NAME WHERE $UUID_COLUMN LIKE :articleUuid")
    fun innerDeleteArticle(articleUuid: String): Int

    @Transaction
    fun importData(articleWithTagsList: List<ArticleWithTags>): Boolean {
        val mutableList = articleWithTagsList.toMutableList()
        articleWithTagsList.forEach { external ->
            getArticleWithTagsList(external.article.article).forEach { internal ->
                mutableList.remove(external)
                if (external.tags.size != internal.tags.size) {
                    val allTags = internal.tags + external.tags
                    allTags.distinctBy { it.tag }
                    deleteArticleWithTags(internal.article.uuid)
                    addArticleWithTags(
                        internal.copy(tags = allTags)
                    )
                }
            }
        }
        mutableList.forEach {
            kotlin.runCatching {
                UUID.fromString(it.article.uuid)
            }.onFailure { _ ->
                it.article.uuid = UUID.randomUUID().toString()
            }
            addArticleWithTags(it)
        }
        return true
    }

    @Transaction
    fun webDavImportData(articleWithTagsList: List<ArticleWithTags>) {
        articleWithTagsList.forEach {
            addArticleWithTags(it)
        }
    }

    @Transaction
    @Query("DELETE FROM $ARTICLE_TABLE_NAME")
    fun deleteAllArticles()
}