package com.skyd.raca.config

import com.skyd.raca.ext.editor
import com.skyd.raca.ext.sharedPreferences
import com.skyd.raca.model.bean.ARTICLE_TABLE_NAME
import com.skyd.raca.model.bean.TAG_TABLE_NAME

var currentArticleId = sharedPreferences().getLong("currentArticleId", 0L)
    set(value) {
        field = value
        sharedPreferences().editor { putLong("currentArticleId", value) }
    }
