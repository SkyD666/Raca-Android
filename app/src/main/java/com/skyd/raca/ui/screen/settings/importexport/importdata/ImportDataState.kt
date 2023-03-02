package com.skyd.raca.ui.screen.settings.importexport.importdata

import com.skyd.raca.base.IUiState

data class ImportDataState(
    val importResultUiState: ImportResultUiState,
) : IUiState

sealed class ImportResultUiState {
    object INIT : ImportResultUiState()
    data class SUCCESS(val time: Long) : ImportResultUiState()
}