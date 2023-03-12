package com.skyd.raca.ui.screen.settings.importexport.file.exportdata

import com.skyd.raca.base.IUiEvent

data class ExportDataEvent(
    val exportResultUiEvent: ExportResultUiEvent? = null,
) : IUiEvent

sealed class ExportResultUiEvent {
    data class SUCCESS(val time: Long) : ExportResultUiEvent()
}