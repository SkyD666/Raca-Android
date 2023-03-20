package com.skyd.raca.ui.screen.settings.data

import com.skyd.raca.base.IUiEvent

data class DataEvent(
    val deleteAllResultUiEvent: DeleteAllResultUiEvent? = null,
) : IUiEvent

sealed class DeleteAllResultUiEvent {
    data class Success(val time: Long) : DeleteAllResultUiEvent()
}