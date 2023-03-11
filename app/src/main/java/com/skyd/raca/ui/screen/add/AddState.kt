package com.skyd.raca.ui.screen.add

import com.skyd.raca.base.IUiState
import com.skyd.raca.model.bean.ArticleWithTags

data class AddState(
    val addArticleResultUiState: AddArticleResultUiState,
    val getArticleWithTagsUiState: GetArticleWithTagsUiState,
) : IUiState

sealed class AddArticleResultUiState {
    object INIT : AddArticleResultUiState()
    object FAILED : AddArticleResultUiState()
    data class SUCCESS(val articleUuid: String) : AddArticleResultUiState()
}

sealed class GetArticleWithTagsUiState {
    object INIT : GetArticleWithTagsUiState()
    object FAILED : GetArticleWithTagsUiState()
    data class SUCCESS(val articleWithTags: ArticleWithTags) : GetArticleWithTagsUiState()
}