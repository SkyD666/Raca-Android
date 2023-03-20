package com.skyd.raca.ui.screen.add

import com.skyd.raca.base.IUiState
import com.skyd.raca.model.bean.ArticleWithTags

data class AddState(
    val getArticleWithTagsUiState: GetArticleWithTagsUiState,
) : IUiState

sealed class GetArticleWithTagsUiState {
    object Init : GetArticleWithTagsUiState()
    object Failed : GetArticleWithTagsUiState()
    data class Success(val articleWithTags: ArticleWithTags) : GetArticleWithTagsUiState()
}