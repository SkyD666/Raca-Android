package com.skyd.raca.model.bean

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable
import java.util.*

const val ARTICLE_TABLE_NAME = "Article"

@Serializable
@Entity(tableName = ARTICLE_TABLE_NAME)
data class ArticleBean(
    @PrimaryKey
    @ColumnInfo(name = UUID_COLUMN)
    var uuid: String,
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
        uuid = UUID.randomUUID().toString(),
        title = title,
        article = article,
        createTime = createTime,
    )

    fun fields(): List<Any> {
        return listOf(uuid, title, article, createTime)
    }

    override fun toString(): String {
        return "$uuid,$title,$article,$createTime"
    }

    companion object {
        const val UUID_COLUMN = "uuid"
        const val TITLE_COLUMN = "title"
        const val ARTICLE_COLUMN = "article"
        const val CREATE_TIME_COLUMN = "createTime"

        val columnName: List<Any> =
            listOf(UUID_COLUMN, TITLE_COLUMN, ARTICLE_COLUMN, CREATE_TIME_COLUMN)
    }
}
