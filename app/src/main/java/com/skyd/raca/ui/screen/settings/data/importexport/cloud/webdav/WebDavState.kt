package com.skyd.raca.ui.screen.settings.data.importexport.cloud.webdav

import com.skyd.raca.base.IUiState
import com.skyd.raca.model.bean.BackupInfo

data class WebDavState(
    val getRemoteRecycleBinResultUiState: GetRemoteRecycleBinResultUiState,
) : IUiState

sealed class GetRemoteRecycleBinResultUiState {
    object INIT : GetRemoteRecycleBinResultUiState()
    data class SUCCESS(val result: List<BackupInfo>) : GetRemoteRecycleBinResultUiState()
}