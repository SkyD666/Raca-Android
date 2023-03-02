package com.skyd.raca.model.bean

import androidx.room.Embedded
import androidx.room.Relation

data class ArticleWithTags(
    @Embedded val article: ArticleBean,
    @Relation(
        parentColumn = "id",
        entityColumn = "articleId"
    )
    val tags: List<TagBean>
)

typealias ArticleWithTags1 = ArticleWithTags