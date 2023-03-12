package com.skyd.raca.ui.screen.settings.importexport.cloud.webdav

import com.skyd.raca.base.BaseViewModel
import com.skyd.raca.base.IUiIntent
import com.skyd.raca.config.refreshArticleData
import com.skyd.raca.model.respository.WebDavRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class WebDavViewModel @Inject constructor(private var webDavRepo: WebDavRepository) :
    BaseViewModel<WebDavState, WebDavEvent, WebDavIntent>() {
    override fun initUiState(): WebDavState {
        return WebDavState(
            getRemoteRecycleBinResultUiState = GetRemoteRecycleBinResultUiState.INIT,
        )
    }

    override fun handleIntent(intent: IUiIntent) {
        when (intent) {
            is WebDavIntent.StartDownload -> {
                requestDataWithFlow(
                    request = {
                        webDavRepo.requestDownload(
                            website = intent.website,
                            username = intent.username,
                            password = intent.password
                        )
                    },
                    successCallback = {
                        sendUiEvent(
                            WebDavEvent(downloadResultUiEvent = DownloadResultUiEvent.SUCCESS(it))
                        )
                        refreshArticleData.tryEmit(Unit)
                    }
                )
            }
            is WebDavIntent.StartUpload -> {
                requestDataWithFlow(
                    request = {
                        webDavRepo.requestUpload(
                            website = intent.website,
                            username = intent.username,
                            password = intent.password
                        )
                    },
                    successCallback = {
                        sendUiEvent(
                            WebDavEvent(uploadResultUiEvent = UploadResultUiEvent.SUCCESS(it))
                        )
                    }
                )
            }
            is WebDavIntent.GetRemoteRecycleBin -> {
                requestDataWithFlow(
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
                requestDataWithFlow(
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
                requestDataWithFlow(
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
