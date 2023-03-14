package com.skyd.raca.ui.screen.home

import com.skyd.raca.base.IUiIntent

sealed class HomeIntent : IUiIntent {
    data class GetArticleDetails(val articleUuid: String) : HomeIntent()
    data class DeleteArticleWithTags(val articleUuid: String) : HomeIntent()
    data class GetArticleWithTagsList(val keyword: String) : HomeIntent()
}