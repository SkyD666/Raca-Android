package com.skyd.raca.model.bean

import androidx.room.Embedded
import androidx.room.Relation
import kotlinx.serialization.Serializable

@Serializable
data class ArticleWithTags(
    @Embedded val article: ArticleBean,
    @Relation(
        parentColumn = ArticleBean.UUID_COLUMN,
        entityColumn = TagBean.ARTICLE_UUID_COLUMN
    )
    val tags: List<TagBean>
)