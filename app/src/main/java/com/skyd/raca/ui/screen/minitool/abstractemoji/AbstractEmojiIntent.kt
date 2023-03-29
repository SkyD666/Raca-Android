package com.skyd.raca.ui.screen.minitool.abstractemoji

import com.skyd.raca.base.IUiIntent

sealed class AbstractEmojiIntent : IUiIntent {
    data class Convert(val article: String) : AbstractEmojiIntent()
}