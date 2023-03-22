package com.skyd.raca.ui.screen.minitool.abstractemoji

import com.skyd.raca.base.IUiState

data class AbstractEmojiState(
    val abstractEmojiResultUiState: AbstractEmojiResultUiState,
) : IUiState

sealed class AbstractEmojiResultUiState {
    object Init : AbstractEmojiResultUiState()
    data class Success(val abstractEmoji: String) : AbstractEmojiResultUiState()
}
