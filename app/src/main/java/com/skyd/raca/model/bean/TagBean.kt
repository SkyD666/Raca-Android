package com.skyd.raca.model.bean

import androidx.room.ColumnInfo
import androidx.room.Entity

const val TAG_TABLE_NAME = "Tag"

private const val ARTICLE_ID_COLUMN = "articleId"
private const val TAG_COLUMN = "tag"
private const val CREATE_TIME_COLUMN = "createTime"

@Entity(tableName = TAG_TABLE_NAME, primaryKeys = ["articleId", "tag"])
data class TagBean(
    @ColumnInfo(name = ARTICLE_ID_COLUMN)
    var articleId: Long,
    @ColumnInfo(name = TAG_COLUMN)
    var tag: String,
    @ColumnInfo(name = CREATE_TIME_COLUMN)
    var createTime: Long,
) : BaseBean {
    constructor(
        tag: String,
    ) : this(
        articleId = 0L,
        tag = tag,
        createTime = System.currentTimeMillis(),
    )

    fun fields(): List<Any> {
        return listOf(articleId, tag, createTime)
    }

    companion object {
        val columnName: List<Any> =
            listOf(ARTICLE_ID_COLUMN, TAG_COLUMN, CREATE_TIME_COLUMN)
    }
}

