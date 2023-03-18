package com.skyd.raca.ui.screen.settings.data

import com.skyd.raca.base.IUiIntent

sealed class DataIntent : IUiIntent {
    object Start : DataIntent()
}