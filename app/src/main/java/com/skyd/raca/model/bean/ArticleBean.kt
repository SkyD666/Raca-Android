package com.skyd.raca.model.bean

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

const val ARTICLE_TABLE_NAME = "Article"

private const val ID_COLUMN = "id"
private const val TITLE_COLUMN = "title"
private const val ARTICLE_COLUMN = "article"
private const val CREATE_TIME_COLUMN = "createTime"

@Entity(tableName = ARTICLE_TABLE_NAME)
data class ArticleBean(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = ID_COLUMN)
    var id: Long,
    @ColumnInfo(name = TITLE_COLUMN)
    var title: String,
    @ColumnInfo(name = ARTICLE_COLUMN)
    var article: String,
    @ColumnInfo(name = CREATE_TIME_COLUMN)
    var createTime: Long,
) : BaseBean {
    constructor(
        title: String,
        article: String,
        createTime: Long = System.currentTimeMillis(),
    ) : this(
        id = 0L,
        title = title,
        article = article,
        createTime = createTime,
    )

    fun fields(): List<Any> {
        return listOf(id, title, article, createTime)
    }

    companion object {
        val columnName: List<Any> =
            listOf(ID_COLUMN, TITLE_COLUMN, ARTICLE_COLUMN, CREATE_TIME_COLUMN)
    }
}
