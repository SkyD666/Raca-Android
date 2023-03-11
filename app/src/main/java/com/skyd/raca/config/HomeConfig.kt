package com.skyd.raca.config

import com.skyd.raca.ext.editor
import com.skyd.raca.ext.sharedPreferences

var currentArticleUuid = sharedPreferences().getString("currentArticleUuid", "").orEmpty()
    set(value) {
        field = value
        sharedPreferences().editor { putString("currentArticleUuid", value) }
    }
