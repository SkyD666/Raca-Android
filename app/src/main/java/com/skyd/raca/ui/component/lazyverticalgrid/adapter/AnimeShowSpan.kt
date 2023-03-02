package com.skyd.raca.ui.component.lazyverticalgrid.adapter

import android.content.res.Configuration
import com.skyd.raca.appContext
import com.skyd.raca.ext.screenIsLand
import com.skyd.raca.model.bean.ArticleBean
import com.skyd.raca.model.bean.ArticleWithTags1
import com.skyd.raca.model.bean.More1Bean

const val MAX_SPAN_SIZE = 60
fun animeShowSpan(
    data: Any,
    enableLandScape: Boolean = true,
    configuration: Configuration = appContext.resources.configuration
): Int = if (enableLandScape && configuration.screenIsLand) {
    when (data) {
        is ArticleWithTags1 -> MAX_SPAN_SIZE / 2
        is More1Bean -> MAX_SPAN_SIZE / 3
        else -> MAX_SPAN_SIZE / 3
    }
} else {
    when (data) {
        is ArticleWithTags1 -> MAX_SPAN_SIZE / 1
        is More1Bean -> MAX_SPAN_SIZE / 2
        else -> MAX_SPAN_SIZE / 1
    }
}