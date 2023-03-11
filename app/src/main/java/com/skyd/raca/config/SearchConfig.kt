package com.skyd.raca.config

import com.skyd.raca.ext.editor
import com.skyd.raca.ext.sharedPreferences
import com.skyd.raca.model.bean.ARTICLE_TABLE_NAME
import com.skyd.raca.model.bean.ArticleBean.Companion.ARTICLE_COLUMN
import com.skyd.raca.model.bean.ArticleBean.Companion.TITLE_COLUMN
import com.skyd.raca.model.bean.ArticleBean.Companion.UUID_COLUMN
import com.skyd.raca.model.bean.TAG_TABLE_NAME
import com.skyd.raca.model.bean.TagBean.Companion.TAG_COLUMN

var useRegexSearch = sharedPreferences().getBoolean("useRegexSearch", false)
    set(value) {
        field = value
        sharedPreferences().editor { putBoolean("useRegexSearch", value) }
    }

val allSearchDomain: HashMap<Pair<String, String>, List<Pair<String, String>>> = hashMapOf(
    (ARTICLE_TABLE_NAME to "段落表") to listOf(
        UUID_COLUMN to "UUID",
        TITLE_COLUMN to "标题",
        ARTICLE_COLUMN to "段落",
    ),
    (TAG_TABLE_NAME to "标签表") to listOf(
//        ARTICLE_ID_COLUMN to "段落ID",
        TAG_COLUMN to "标签",
//        CREATE_TIME_COLUMN to "创建时间"
    ),
)

fun setSearchDomain(
    tableName: String,
    columnName: String,
    search: Boolean
) {
    sharedPreferences("SearchDomain").editor {
        putBoolean("${tableName}/${columnName}", search)
    }
}

fun getSearchDomain(
    tableName: String,
    columnName: String,
): Boolean {
    return sharedPreferences("SearchDomain")
        .getBoolean(
            "${tableName}/${columnName}",
            if (tableName == ARTICLE_TABLE_NAME && (columnName == TITLE_COLUMN || columnName == ARTICLE_COLUMN)) {
                true
            } else tableName == TAG_TABLE_NAME && columnName == TAG_COLUMN
        )
}

