package com.skyd.raca.ui.screen.settings.searchconfig

import com.skyd.raca.base.IUiState

data class SearchConfigState(
    val searchDomainResultUiState: SearchDomainResultUiState,
) : IUiState

sealed class SearchDomainResultUiState {
    object INIT : SearchDomainResultUiState()
    data class SUCCESS(val searchDomainMap: Map<String, Boolean>) : SearchDomainResultUiState()
}