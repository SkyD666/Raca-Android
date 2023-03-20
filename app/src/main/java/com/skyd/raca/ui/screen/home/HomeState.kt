package com.skyd.raca.ui.screen.home

import com.skyd.raca.base.IUiState
import com.skyd.raca.model.bean.ArticleWithTags
import com.skyd.raca.model.preference.CurrentArticleUuidPreference

data class HomeState(
    val articleDetailUiState: ArticleDetailUiState,
    val searchResultUiState: SearchResultUiState,
) : IUiState

sealed class ArticleDetailUiState {
    data class Init(val articleUuid: String = CurrentArticleUuidPreference.default) : ArticleDetailUiState()
    data class Success(val articleWithTags: ArticleWithTags) : ArticleDetailUiState()
}

sealed class SearchResultUiState {
    object Init : SearchResultUiState()
    data class Success(val articleWithTagsList: List<ArticleWithTags>) : SearchResultUiState()
}