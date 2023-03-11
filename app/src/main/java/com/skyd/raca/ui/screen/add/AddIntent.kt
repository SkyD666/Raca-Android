package com.skyd.raca.ui.screen.add

import com.skyd.raca.base.IUiIntent
import com.skyd.raca.model.bean.ArticleWithTags

sealed class AddIntent : IUiIntent {
    data class AddNewArticleWithTags(val articleWithTags: ArticleWithTags) : AddIntent()
    data class GetArticleWithTags(val articleUuid: String) : AddIntent()
}