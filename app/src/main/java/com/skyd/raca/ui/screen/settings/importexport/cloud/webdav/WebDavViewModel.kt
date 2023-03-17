package com.skyd.raca.ui.screen.settings.importexport.cloud.webdav

import com.skyd.raca.base.BaseViewModel
import com.skyd.raca.base.IUIChange
import com.skyd.raca.config.refreshArticleData
import com.skyd.raca.model.respository.WebDavRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class WebDavViewModel @Inject constructor(private var webDavRepo: WebDavRepository) :
    BaseViewModel<WebDavState, WebDavEvent, WebDavIntent>() {
    override fun initUiState(): WebDavState {
        return WebDavState(
            getRemoteRecycleBinResultUiState = GetRemoteRecycleBinResultUiState.INIT,
        )
    }

    override fun IUIChange.checkStateOrEvent() = this as? WebDavState to this as? WebDavEvent

    override fun Flow<WebDavIntent>.handleIntent(): Flow<IUIChange> = merge(
        filterIsInstance<WebDavIntent.StartDownload>().flatMapConcat { intent ->
            webDavRepo.requestDownload(
                website = intent.website, username = intent.username, password = intent.password
            )
                .mapToUIChange { data ->
                    WebDavEvent(downloadResultUiEvent = DownloadResultUiEvent.SUCCESS(data))
                }
                .defaultFinally()
                .onCompletion {
                    refreshArticleData.tryEmit(Unit)
                }
        },

        filterIsInstance<WebDavIntent.StartUpload>().flatMapConcat { intent ->
            webDavRepo.requestUpload(
                website = intent.website, username = intent.username, password = intent.password
            )
                .mapToUIChange { data ->
                    WebDavEvent(uploadResultUiEvent = UploadResultUiEvent.SUCCESS(data))
                }
                .defaultFinally()
        },

        filterIsInstance<WebDavIntent.GetRemoteRecycleBin>().flatMapConcat { intent ->
            webDavRepo.requestRemoteRecycleBin(
                website = intent.website, username = intent.username, password = intent.password
            )
                .mapToUIChange { data ->
                    copy(
                        getRemoteRecycleBinResultUiState =
                        GetRemoteRecycleBinResultUiState.SUCCESS(data)
                    )
                }
                .defaultFinally()
        },

        filterIsInstance<WebDavIntent.DeleteFromRemoteRecycleBin>().flatMapConcat { intent ->
            webDavRepo.requestDeleteFromRemoteRecycleBin(
                website = intent.website,
                username = intent.username,
                password = intent.password,
                uuid = intent.uuid
            )
            webDavRepo.requestRemoteRecycleBin(
                website = intent.website, username = intent.username, password = intent.password
            )
                .mapToUIChange { data ->
                    copy(
                        getRemoteRecycleBinResultUiState =
                        GetRemoteRecycleBinResultUiState.SUCCESS(data)
                    )
                }
                .defaultFinally()
        },

        filterIsInstance<WebDavIntent.ClearRemoteRecycleBin>().flatMapConcat { intent ->
            webDavRepo.requestClearRemoteRecycleBin(
                website = intent.website, username = intent.username, password = intent.password,
            )
            webDavRepo.requestRemoteRecycleBin(
                website = intent.website, username = intent.username, password = intent.password
            )
                .mapToUIChange { data ->
                    copy(
                        getRemoteRecycleBinResultUiState =
                        GetRemoteRecycleBinResultUiState.SUCCESS(data)
                    )
                }
                .defaultFinally()
        },
    )
}
