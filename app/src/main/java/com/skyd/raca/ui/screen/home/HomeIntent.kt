package com.skyd.raca.ui.screen.home

import com.skyd.raca.base.IUiIntent
import com.skyd.raca.model.bean.ArticleWithTags

sealed class HomeIntent : IUiIntent {
    data class GetArticleDetails(val articleId: Long) : HomeIntent()
    data class DeleteArticleWithTags(val articleId: Long) : HomeIntent()
    data class GetArticleWithTagsList(val keyword: String) : HomeIntent()
    data class AddNewArticleWithTags(val articleWithTags: ArticleWithTags) : HomeIntent()
}