package com.skyd.raca.db.dao

import androidx.room.*
import androidx.sqlite.db.SupportSQLiteQuery
import com.skyd.raca.db.appDataBase
import com.skyd.raca.model.bean.ARTICLE_TABLE_NAME
import com.skyd.raca.model.bean.ArticleBean
import com.skyd.raca.model.bean.ArticleWithTags

@Dao
interface ArticleDao {
    @Transaction
    @RawQuery
    fun getArticleWithTagsList(sql: SupportSQLiteQuery): List<ArticleWithTags>

    @Query("SELECT * FROM $ARTICLE_TABLE_NAME")
    fun getArticleList(): List<ArticleBean>

    @Transaction
    @Query("SELECT * FROM $ARTICLE_TABLE_NAME WHERE id = :articleId")
    fun getArticleWithTags(articleId: Long): ArticleWithTags

    @Transaction
    @Query("SELECT * FROM $ARTICLE_TABLE_NAME WHERE article LIKE :article")
    fun getArticleWithTagsList(article: String): List<ArticleWithTags>

    fun addArticleWithTags(articleWithTags: ArticleWithTags): Long {
        val articleId = addArticle(articleWithTags.article)
        articleWithTags.tags.forEach {
            it.articleId = articleId
        }
        appDataBase.tagDao().apply {
            deleteTags(articleId)
            addTags(articleWithTags.tags)
        }
        return articleId
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addArticle(article: ArticleBean): Long

    fun deleteArticleWithTags(articleId: Long): Int {
        appDataBase.tagDao().deleteTags(articleId)
        return innerDeleteArticle(articleId)
    }

    @Query("DELETE FROM $ARTICLE_TABLE_NAME WHERE id = :articleId")
    fun innerDeleteArticle(articleId: Long): Int

    fun importData(articleWithTagsList: List<ArticleWithTags>): Boolean {
        val mutableList = articleWithTagsList.toMutableList()
        articleWithTagsList.forEach { external ->
            getArticleWithTagsList(external.article.article).forEach { internal ->
                mutableList.remove(external)
                if (external.tags.size != internal.tags.size) {
                    val allTags = internal.tags + external.tags
                    allTags.distinctBy { it.tag }
                    deleteArticleWithTags(internal.article.id)
                    addArticleWithTags(
                        internal.copy(tags = allTags)
                    )
                }
            }
        }
        mutableList.forEach {
            addArticleWithTags(it.copy(article = it.article.copy(id = 0L)))
        }
        return true
    }
}