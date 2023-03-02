package com.skyd.raca.config

import com.skyd.raca.ext.editor
import com.skyd.raca.ext.sharedPreferences
import com.skyd.raca.model.bean.ARTICLE_TABLE_NAME
import com.skyd.raca.model.bean.TAG_TABLE_NAME

var useRegexSearch = sharedPreferences().getBoolean("useRegexSearch", false)
    set(value) {
        field = value
        sharedPreferences().editor { putBoolean("useRegexSearch", value) }
    }

val allSearchDomain: HashMap<Pair<String, String>, List<Pair<String, String>>> = hashMapOf(
    (ARTICLE_TABLE_NAME to "段落表") to listOf(
        "id" to "ID",
        "title" to "标题",
        "article" to "段落",
        "createTime" to "创建时间"
    ),
    (TAG_TABLE_NAME to "标签表") to listOf(
        "articleId" to "段落ID",
        "tag" to "标签",
        "createTime" to "创建时间"
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
        .getBoolean("${tableName}/${columnName}", false)
}

