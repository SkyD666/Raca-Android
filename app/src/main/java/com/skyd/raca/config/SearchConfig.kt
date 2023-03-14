package com.skyd.raca.config

import com.skyd.raca.model.bean.ARTICLE_TABLE_NAME
import com.skyd.raca.model.bean.ArticleBean.Companion.ARTICLE_COLUMN
import com.skyd.raca.model.bean.ArticleBean.Companion.TITLE_COLUMN
import com.skyd.raca.model.bean.ArticleBean.Companion.UUID_COLUMN
import com.skyd.raca.model.bean.TAG_TABLE_NAME
import com.skyd.raca.model.bean.TagBean.Companion.TAG_COLUMN

val allSearchDomain: HashMap<Pair<String, String>, List<Pair<String, String>>> = hashMapOf(
    (ARTICLE_TABLE_NAME to "段落表") to listOf(
        UUID_COLUMN to "UUID",
        TITLE_COLUMN to "标题",
        ARTICLE_COLUMN to "段落",
    ),
    (TAG_TABLE_NAME to "标签表") to listOf(
        TAG_COLUMN to "标签",
    ),
)
