package com.skyd.raca.model.bean

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import kotlinx.serialization.Serializable

const val TAG_TABLE_NAME = "Tag"

@Serializable
@Entity(
    tableName = TAG_TABLE_NAME,
    primaryKeys = [TagBean.ARTICLE_UUID_COLUMN, TagBean.TAG_COLUMN],
    foreignKeys = [
        ForeignKey(
            entity = ArticleBean::class,
            parentColumns = [ArticleBean.UUID_COLUMN],
            childColumns = [TagBean.ARTICLE_UUID_COLUMN],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class TagBean(
    @ColumnInfo(name = ARTICLE_UUID_COLUMN)
    var articleUuid: String,
    @ColumnInfo(name = TAG_COLUMN)
    var tag: String,
    @ColumnInfo(name = CREATE_TIME_COLUMN)
    var createTime: Long,
) : BaseBean {
    constructor(
        tag: String,
    ) : this(
        articleUuid = "",
        tag = tag,
        createTime = System.currentTimeMillis(),
    )

    fun fields(): List<Any> {
        return listOf(articleUuid, tag, createTime)
    }

    override fun toString(): String {
        return "$articleUuid,$tag,$createTime"
    }

    companion object {
        const val ARTICLE_UUID_COLUMN = "articleUuid"
        const val TAG_COLUMN = "tag"
        const val CREATE_TIME_COLUMN = "createTime"

        val columnName: List<Any> =
            listOf(ARTICLE_UUID_COLUMN, TAG_COLUMN, CREATE_TIME_COLUMN)
    }
}

