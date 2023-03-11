package com.skyd.raca.ui.screen.settings.importexport.cloud.webdav

import com.skyd.raca.base.IUiState
import com.skyd.raca.model.bean.BackupInfo
import com.skyd.raca.model.bean.WebDavResultInfo

data class WebDavState(
    val uploadResultUiState: UploadResultUiState,
    val downloadResultUiState: DownloadResultUiState,
    val getRemoteRecycleBinResultUiState: GetRemoteRecycleBinResultUiState,
) : IUiState

sealed class UploadResultUiState {
    object INIT : UploadResultUiState()
    data class SUCCESS(val result: WebDavResultInfo) : UploadResultUiState()
}

sealed class DownloadResultUiState {
    object INIT : DownloadResultUiState()
    data class SUCCESS(val result: WebDavResultInfo) : DownloadResultUiState()
}

sealed class GetRemoteRecycleBinResultUiState {
    object INIT : GetRemoteRecycleBinResultUiState()
    data class SUCCESS(val result: List<BackupInfo>) : GetRemoteRecycleBinResultUiState()
}