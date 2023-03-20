package com.skyd.raca.ui.screen.settings.data.importexport.file.importdata

import com.skyd.raca.base.IUiEvent

data class ImportDataEvent(
    val importResultUiEvent: ImportResultUiEvent? = null,
) : IUiEvent

sealed class ImportResultUiEvent {
    data class Success(val time: Long) : ImportResultUiEvent()
}