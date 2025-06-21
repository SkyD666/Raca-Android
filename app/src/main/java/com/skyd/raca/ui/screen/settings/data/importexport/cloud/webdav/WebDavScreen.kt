package com.skyd.raca.ui.screen.settings.data.importexport.cloud.webdav

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.CloudDownload
import androidx.compose.material.icons.outlined.CloudUpload
import androidx.compose.material.icons.outlined.DeleteForever
import androidx.compose.material.icons.outlined.Dns
import androidx.compose.material.icons.outlined.Key
import androidx.compose.material.icons.outlined.Recycling
import androidx.compose.material.icons.outlined.RestoreFromTrash
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.skyd.raca.R
import com.skyd.raca.appContext
import com.skyd.raca.base.LoadUiIntent
import com.skyd.raca.ext.dateTime
import com.skyd.raca.ext.editor
import com.skyd.raca.ext.secretSharedPreferences
import com.skyd.raca.model.bean.BackupInfo
import com.skyd.raca.model.bean.WebDavResultInfo
import com.skyd.raca.model.bean.WebDavWaitingInfo
import com.skyd.raca.model.preference.WebDavServerPreference
import com.skyd.raca.ui.component.RacaIconButton
import com.skyd.raca.ui.component.RacaLottieAnimation
import com.skyd.raca.ui.component.RacaTopBar
import com.skyd.raca.ui.component.RacaTopBarStyle
import com.skyd.raca.ui.component.dialog.DeleteWarningDialog
import com.skyd.raca.ui.component.dialog.TextFieldDialog
import com.skyd.raca.ui.component.dialog.WaitingDialog
import com.skyd.raca.ui.local.LocalWebDavServer
import com.skyd.settings.BaseSettingsItem
import com.skyd.settings.SettingsLazyColumn
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import org.koin.compose.viewmodel.koinViewModel


@Serializable
data object WebDavRoute

@Composable
fun WebDavScreen(viewModel: WebDavViewModel = koinViewModel()) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var openWaitingDialog by remember { mutableStateOf(false) }
    var waitingDialogCurrent by remember { mutableStateOf<Int?>(null) }
    var waitingDialogTotal by remember { mutableStateOf<Int?>(null) }
    var openDeleteWarningDialog by rememberSaveable { mutableStateOf<String?>(null) }
    var openInputDialog by remember { mutableStateOf(false) }
    var inputDialogInfo by remember {
        mutableStateOf<Triple<String, String, (String) -> Unit>>(Triple("", "") {})
    }
    var inputDialogIsPassword by remember { mutableStateOf(false) }
    var openRecycleBinBottomSheet by rememberSaveable { mutableStateOf(false) }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            RacaTopBar(
                style = RacaTopBarStyle.LargeFlexible,
                scrollBehavior = scrollBehavior,
                title = { Text(text = stringResource(R.string.webdav_screen_name)) },
            )
        }
    ) { paddingValues ->
        val server = LocalWebDavServer.current
        var account by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }

        LaunchedEffect(Unit) {
            account = secretSharedPreferences().getString("webDavAccount", null).orEmpty()
            password = secretSharedPreferences().getString("webDavPassword", null).orEmpty()
        }

        SettingsLazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .nestedScroll(scrollBehavior.nestedScrollConnection),
            contentPadding = paddingValues,
        ) {
            group(text = { context.getString(R.string.webdav_screen_service_category) }) {
                item {
                    BaseSettingsItem(
                        icon = rememberVectorPainter(image = Icons.Outlined.Dns),
                        text = stringResource(id = R.string.webdav_screen_server),
                        descriptionText = server.ifBlank {
                            stringResource(id = R.string.webdav_screen_server_description)
                        },
                        onClick = {
                            inputDialogInfo = Triple(
                                appContext.getString(R.string.webdav_screen_input_server), server
                            ) {
                                openInputDialog = false
                                WebDavServerPreference.put(
                                    context = context,
                                    scope = scope,
                                    value = if (!it.endsWith("/")) "$it/" else it
                                )
                            }
                            inputDialogIsPassword = false
                            openInputDialog = true
                        }
                    )
                }
                item {
                    BaseSettingsItem(
                        icon = rememberVectorPainter(image = Icons.Outlined.AccountCircle),
                        text = stringResource(id = R.string.webdav_screen_account),
                        descriptionText = account.ifBlank {
                            stringResource(id = R.string.webdav_screen_account_description)
                        },
                        onClick = {
                            inputDialogInfo = Triple(
                                appContext.getString(R.string.webdav_screen_input_account), account
                            ) {
                                account = it
                                openInputDialog = false
                                secretSharedPreferences().editor { putString("webDavAccount", it) }
                            }
                            inputDialogIsPassword = false
                            openInputDialog = true
                        }
                    )
                }
                item {
                    BaseSettingsItem(
                        icon = rememberVectorPainter(image = Icons.Outlined.Key),
                        text = stringResource(id = R.string.webdav_screen_password),
                        descriptionText = stringResource(
                            id = if (password.isBlank()) R.string.webdav_screen_password_description
                            else R.string.webdav_screen_password_entered
                        ),
                        onClick = {
                            inputDialogInfo = Triple(
                                appContext.getString(R.string.webdav_screen_input_password),
                                password
                            ) {
                                password = it
                                openInputDialog = false
                                secretSharedPreferences().editor { putString("webDavPassword", it) }
                            }
                            inputDialogIsPassword = true
                            openInputDialog = true
                        }
                    )
                }
            }
            group(text = { context.getString(R.string.webdav_screen_sync_category) }) {
                val webDavIncompleteInfo = {
                    scope.launch {
                        snackbarHostState.showSnackbar(
                            message = appContext.getString(R.string.webdav_screen_info_incomplete),
                            withDismissAction = true
                        )
                    }
                }
                item {
                    BaseSettingsItem(
                        icon = rememberVectorPainter(image = Icons.Outlined.CloudDownload),
                        text = stringResource(id = R.string.webdav_screen_download),
                        descriptionText = stringResource(id = R.string.webdav_screen_download_description),
                        onClick = {
                            if (server.isNotBlank() && account.isNotBlank() && password.isNotBlank()) {
                                viewModel.sendUiIntent(
                                    WebDavIntent.StartDownload(
                                        website = server, username = account, password = password
                                    )
                                )
                            } else {
                                webDavIncompleteInfo()
                            }
                        }
                    )
                }
                item {
                    BaseSettingsItem(
                        icon = rememberVectorPainter(image = Icons.Outlined.CloudUpload),
                        text = stringResource(id = R.string.webdav_screen_upload),
                        descriptionText = stringResource(id = R.string.webdav_screen_upload_description),
                        onClick = {
                            if (server.isNotBlank() && account.isNotBlank() && password.isNotBlank()) {
                                viewModel.sendUiIntent(
                                    WebDavIntent.StartUpload(
                                        website = server, username = account, password = password
                                    )
                                )
                            } else {
                                webDavIncompleteInfo()
                            }
                        }
                    )
                }
                item {
                    BaseSettingsItem(
                        icon = rememberVectorPainter(image = Icons.Outlined.Recycling),
                        text = stringResource(id = R.string.webdav_screen_remote_recycle_bin),
                        descriptionText = stringResource(id = R.string.webdav_screen_remote_recycle_bin_description),
                        onClick = {
                            if (server.isNotBlank() && account.isNotBlank() && password.isNotBlank()) {
                                viewModel.sendUiIntent(
                                    WebDavIntent.GetRemoteRecycleBin(
                                        website = server, username = account, password = password
                                    )
                                )
                                openRecycleBinBottomSheet = true
                            } else {
                                webDavIncompleteInfo()
                            }
                        }
                    )
                }
            }
        }

        viewModel.loadUiIntentFlow.collectAsStateWithLifecycle(initialValue = null).value?.also { loadUiIntent ->
            when (loadUiIntent) {
                is LoadUiIntent.Error -> {
                    scope.launch {
                        snackbarHostState.showSnackbar(
                            message = appContext.getString(
                                R.string.webdav_screen_failed,
                                loadUiIntent.msg
                            ),
                            withDismissAction = true
                        )
                    }
                }

                is LoadUiIntent.ShowMainView -> {}
                is LoadUiIntent.Loading -> {
                    openWaitingDialog = loadUiIntent.isShow
                    waitingDialogCurrent = null
                    waitingDialogTotal = null
                }
            }
        }

        WaitingDialog(
            visible = openWaitingDialog,
            currentValue = waitingDialogCurrent,
            totalValue = waitingDialogTotal
        )
        DeleteWarningDialog(
            visible = openDeleteWarningDialog != null,
            onDismissRequest = { openDeleteWarningDialog = null },
            onDismiss = { openDeleteWarningDialog = null },
            onConfirm = {
                if (openDeleteWarningDialog.isNullOrBlank()) {
                    viewModel.sendUiIntent(
                        WebDavIntent.ClearRemoteRecycleBin(
                            website = server, username = account, password = password,
                        )
                    )
                } else {
                    viewModel.sendUiIntent(
                        WebDavIntent.DeleteFromRemoteRecycleBin(
                            website = server,
                            username = account,
                            password = password,
                            uuid = openDeleteWarningDialog!!
                        )
                    )
                }
                openDeleteWarningDialog = null
            }
        )
        TextFieldDialog(
            visible = openInputDialog,
            title = inputDialogInfo.first,
            value = inputDialogInfo.second,
            maxLines = 1,
            isPassword = inputDialogIsPassword,
            onDismissRequest = { openInputDialog = false },
            onConfirm = inputDialogInfo.third,
            onValueChange = {
                inputDialogInfo = inputDialogInfo.copy(second = it)
            },
        )
        if (openRecycleBinBottomSheet) {
            RecycleBinBottomSheet(
                onDismissRequest = { openRecycleBinBottomSheet = false },
                onRestore = {
                    viewModel.sendUiIntent(
                        WebDavIntent.RestoreFromRemoteRecycleBin(
                            website = server, username = account, password = password, uuid = it
                        )
                    )
                },
                onDelete = { openDeleteWarningDialog = it },
                onClear = { openDeleteWarningDialog = "" }
            )
        }
    }
    viewModel.uiEventFlow.collectAsStateWithLifecycle(initialValue = null).value?.apply {
        when (uploadResultUiEvent) {
            is UploadResultUiEvent.Success -> {
                when (val result = uploadResultUiEvent.result) {
                    is WebDavResultInfo -> {
                        scope.launch {
                            snackbarHostState.showSnackbar(
                                message = appContext.getString(
                                    R.string.webdav_screen_upload_success,
                                    result.time / 1000.0f, result.count
                                ),
                                withDismissAction = true
                            )
                        }
                    }

                    is WebDavWaitingInfo -> {
                        waitingDialogCurrent = result.current
                        waitingDialogTotal = result.total
                    }
                }
            }

            null -> {}
        }
        when (downloadResultUiEvent) {
            is DownloadResultUiEvent.Success -> {
                val state = downloadResultUiEvent.result
                if (state is WebDavResultInfo) {
                    scope.launch {
                        snackbarHostState.showSnackbar(
                            message = appContext.getString(
                                R.string.webdav_screen_download_success,
                                state.time / 1000.0f, state.count
                            ),
                            withDismissAction = true
                        )
                    }
                } else if (state is WebDavWaitingInfo) {
                    waitingDialogCurrent = state.current
                    waitingDialogTotal = state.total
                }
            }

            null -> {}
        }
    }
}

@Composable
private fun RecycleBinBottomSheet(
    onDismissRequest: () -> Unit,
    onRestore: (String) -> Unit,
    onDelete: (String) -> Unit,
    onClear: () -> Unit,
    viewModel: WebDavViewModel = koinViewModel(),
) {
    var list: List<BackupInfo>

    viewModel.uiStateFlow.collectAsStateWithLifecycle().value.apply {
        list = when (getRemoteRecycleBinResultUiState) {
            GetRemoteRecycleBinResultUiState.Init -> emptyList()
            is GetRemoteRecycleBinResultUiState.Success -> getRemoteRecycleBinResultUiState.result
        }
    }

    ModalBottomSheet(onDismissRequest = onDismissRequest) {
        Row(
            Modifier
                .padding(horizontal = 16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                text = stringResource(R.string.webdav_screen_remote_recycle_bin),
                style = MaterialTheme.typography.titleLarge
            )
            Button(onClick = onClear) {
                Text(text = stringResource(R.string.webdav_screen_clear_remote_recycle_bin))
            }
        }
        LazyColumn(contentPadding = PaddingValues(vertical = 10.dp)) {
            if (list.isEmpty()) {
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        RacaLottieAnimation(
                            modifier = Modifier.size(48.dp),
                            resId = R.raw.lottie_genshin_impact_paimon_1
                        )
                        Text(
                            modifier = Modifier.padding(start = 10.dp),
                            textAlign = TextAlign.Center,
                            text = stringResource(R.string.webdav_screen_remote_recycle_bin_is_empty)
                        )
                    }
                }
            }
            items(list.size) {
                ListItem(
                    headlineContent = { Text(text = list[it].uuid) },
                    supportingContent = {
                        Text(
                            text = stringResource(
                                R.string.webdav_screen_last_modified_time,
                                dateTime(list[it].modifiedTime)
                            )
                        )
                    },
                    trailingContent = {
                        Row {
                            RacaIconButton(
                                imageVector = Icons.Outlined.RestoreFromTrash,
                                contentDescription = stringResource(R.string.webdav_screen_restore),
                                onClick = { onRestore(list[it].uuid) }
                            )
                            RacaIconButton(
                                imageVector = Icons.Outlined.DeleteForever,
                                contentDescription = stringResource(R.string.webdav_screen_delete),
                                onClick = { onDelete(list[it].uuid) }
                            )
                        }
                    }
                )
                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
            }
        }
    }
}