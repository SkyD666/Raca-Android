package com.skyd.raca.ui.screen.add

import com.skyd.raca.base.IUiEvent

data class AddEvent(
    val addArticleResultUiEvent: AddArticleResultUiEvent? = null,
) : IUiEvent

sealed class AddArticleResultUiEvent {
    object FAILED : AddArticleResultUiEvent()
    data class SUCCESS(val articleUuid: String) : AddArticleResultUiEvent()
}