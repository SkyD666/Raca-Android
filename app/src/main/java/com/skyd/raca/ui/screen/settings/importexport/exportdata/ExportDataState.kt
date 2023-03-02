package com.skyd.raca.ui.screen.settings.importexport.exportdata

import com.skyd.raca.base.IUiState

data class ExportDataState(
    val exportResultUiState: ExportResultUiState,
) : IUiState

sealed class ExportResultUiState {
    object INIT : ExportResultUiState()
    data class SUCCESS(val time: Long) : ExportResultUiState()
}