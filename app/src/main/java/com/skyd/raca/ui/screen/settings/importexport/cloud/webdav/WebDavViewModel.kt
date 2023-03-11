package com.skyd.raca.ui.screen.settings.importexport.cloud.webdav

import com.skyd.raca.base.BaseViewModel
import com.skyd.raca.base.IUiIntent
import com.skyd.raca.model.respository.WebDavRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class WebDavViewModel @Inject constructor(var webDavRepo: WebDavRepository) :
    BaseViewModel<WebDavState, WebDavIntent>() {
    override fun initUiState(): WebDavState {
        return WebDavState(
            uploadResultUiState = UploadResultUiState.INIT,
            downloadResultUiState = DownloadResultUiState.INIT,
            getRemoteRecycleBinResultUiState = GetRemoteRecycleBinResultUiState.INIT,
        )
    }

    override fun handleIntent(intent: IUiIntent) {
        when (intent) {
            is WebDavIntent.StartDownload -> {
                requestDataWithFlow(showLoading = true,
                    request = {
                        webDavRepo.requestDownload(
                            website = intent.website,
                            username = intent.username,
                            password = intent.password
                        )
                    },
                    successCallback = {
                        sendUiState {
                            copy(downloadResultUiState = DownloadResultUiState.SUCCESS(it))
                        }
                    }
                )
            }
            is WebDavIntent.StartUpload -> {
                requestDataWithFlow(showLoading = true,
                    request = {
                        webDavRepo.requestUpload(
                            website = intent.website,
                            username = intent.username,
                            password = intent.password
                        )
                    },
                    successCallback = {
                        sendUiState {
                            copy(uploadResultUiState = UploadResultUiState.SUCCESS(it))
                        }
                    }
                )
            }
            is WebDavIntent.GetRemoteRecycleBin -> {
                requestDataWithFlow(showLoading = true,
                    request = {
                        webDavRepo.requestRemoteRecycleBin(
                            website = intent.website,
                            username = intent.username,
                            password = intent.password
                        )
                    },
                    successCallback = {
                        sendUiState {
                            copy(
                                getRemoteRecycleBinResultUiState =
                                GetRemoteRecycleBinResultUiState.SUCCESS(it)
                            )
                        }
                    }
                )
            }
            is WebDavIntent.DeleteFromRemoteRecycleBin -> {
                requestDataWithFlow(showLoading = true,
                    request = {
                        webDavRepo.requestDeleteFromRemoteRecycleBin(
                            website = intent.website,
                            username = intent.username,
                            password = intent.password,
                            uuid = intent.uuid
                        )
                        webDavRepo.requestRemoteRecycleBin(
                            website = intent.website,
                            username = intent.username,
                            password = intent.password
                        )
                    },
                    successCallback = {
                        sendUiState {
                            copy(
                                getRemoteRecycleBinResultUiState =
                                GetRemoteRecycleBinResultUiState.SUCCESS(it)
                            )
                        }
                    }
                )
            }
            is WebDavIntent.ClearRemoteRecycleBin -> {
                requestDataWithFlow(showLoading = true,
                    request = {
                        webDavRepo.requestClearRemoteRecycleBin(
                            website = intent.website,
                            username = intent.username,
                            password = intent.password,
                        )
                        webDavRepo.requestRemoteRecycleBin(
                            website = intent.website,
                            username = intent.username,
                            password = intent.password
                        )
                    },
                    successCallback = {
                        sendUiState {
                            copy(
                                getRemoteRecycleBinResultUiState =
                                GetRemoteRecycleBinResultUiState.SUCCESS(it)
                            )
                        }
                    }
                )
            }
        }
    }
}
