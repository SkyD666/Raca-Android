package com.skyd.raca.ui.screen.home

import com.skyd.raca.base.IUiState
import com.skyd.raca.model.bean.ArticleWithTags
import com.skyd.raca.model.preference.CurrentArticleUuidPreference

data class HomeState(
    val articleDetailUiState: ArticleDetailUiState,
    val searchResultUiState: SearchResultUiState,
) : IUiState

sealed class ArticleDetailUiState {
    data class INIT(val articleUuid: String = CurrentArticleUuidPreference.default) : ArticleDetailUiState()
    data class SUCCESS(val articleWithTags: ArticleWithTags) : ArticleDetailUiState()
}

sealed class SearchResultUiState {
    object INIT : SearchResultUiState()
    data class SUCCESS(val articleWithTagsList: List<ArticleWithTags>) : SearchResultUiState()
}