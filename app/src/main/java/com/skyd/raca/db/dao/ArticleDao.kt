package com.skyd.raca.db.dao

import androidx.room.*
import androidx.sqlite.db.SupportSQLiteQuery
import com.skyd.raca.db.appDataBase
import com.skyd.raca.model.bean.ARTICLE_TABLE_NAME
import com.skyd.raca.model.bean.ArticleBean
import com.skyd.raca.model.bean.ArticleWithTags
import java.util.*

@Dao
interface ArticleDao {
    @Transaction
    @RawQuery
    fun getArticleWithTagsList(sql: SupportSQLiteQuery): List<ArticleWithTags>

    @Transaction
    @Query("SELECT * FROM $ARTICLE_TABLE_NAME")
    fun getAllArticleWithTagsList(): List<ArticleWithTags>

    @Query("SELECT * FROM $ARTICLE_TABLE_NAME")
    fun getArticleList(): List<ArticleBean>

    @Transaction
    @Query("SELECT * FROM $ARTICLE_TABLE_NAME WHERE uuid LIKE :articleUuid")
    fun getArticleWithTags(articleUuid: String): ArticleWithTags

    @Transaction
    @Query("SELECT * FROM $ARTICLE_TABLE_NAME WHERE article LIKE :article")
    fun getArticleWithTagsList(article: String): List<ArticleWithTags>

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
        appDataBase.tagDao().apply {
            deleteTags(articleUuid)
            addTags(articleWithTags.tags)
        }
        return articleUuid
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun innerAddArticle(article: ArticleBean)

    @Transaction
    fun deleteArticleWithTags(articleUuid: String): Int {
        appDataBase.tagDao().deleteTags(articleUuid)
        return innerDeleteArticle(articleUuid)
    }

    @Query("DELETE FROM $ARTICLE_TABLE_NAME WHERE uuid LIKE :articleUuid")
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
}