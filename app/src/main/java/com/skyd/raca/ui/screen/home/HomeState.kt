package com.skyd.raca.ui.screen.home

import com.skyd.raca.base.IUiState
import com.skyd.raca.model.bean.ArticleWithTags

data class HomeState(
    val articleDetailUiState: ArticleDetailUiState,
    val searchResultUiState: SearchResultUiState,
) : IUiState

sealed class ArticleDetailUiState {
    data class INIT(val articleId: Long) : ArticleDetailUiState()
    data class SUCCESS(val articleWithTags: ArticleWithTags) : ArticleDetailUiState()
}

sealed class SearchResultUiState {
    object INIT : SearchResultUiState()
    data class SUCCESS(val articleWithTagsList: List<ArticleWithTags>) : SearchResultUiState()
}