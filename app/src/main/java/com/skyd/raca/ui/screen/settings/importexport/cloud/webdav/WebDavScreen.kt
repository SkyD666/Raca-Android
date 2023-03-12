package com.skyd.raca.ui.screen.settings.importexport.cloud.webdav

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.skyd.raca.R
import com.skyd.raca.appContext
import com.skyd.raca.base.LoadUiIntent
import com.skyd.raca.ext.dateTime
import com.skyd.raca.ext.editor
import com.skyd.raca.ext.secretSharedPreferences
import com.skyd.raca.ext.sharedPreferences
import com.skyd.raca.model.bean.BackupInfo
import com.skyd.raca.ui.component.*
import com.skyd.raca.ui.local.LocalNavController
import kotlinx.coroutines.launch

const val WEBDAV_SCREEN_ROUTE = "WebDavScreen"

@Composable
fun WebDavScreen(viewModel: WebDavViewModel = hiltViewModel()) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val navController = LocalNavController.current
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    var openWaitingDialog by remember { mutableStateOf(false) }
    var openDeleteWarningDialog by rememberSaveable { mutableStateOf<String?>(null) }
    var openInputDialog by remember { mutableStateOf<Triple<String, String, (String) -> Unit>?>(null) }
    var openRecycleBinBottomSheet by rememberSaveable { mutableStateOf(false) }

    viewModel.uiEventFlow.collectAsStateWithLifecycle(initialValue = null).value?.apply {
        when (uploadResultUiEvent) {
            is UploadResultUiEvent.SUCCESS -> {
                scope.launch {
                    snackbarHostState.showSnackbar(
                        message = appContext.getString(
                            R.string.webdav_screen_upload_success,
                            uploadResultUiEvent.result.time / 1000.0f,
                            uploadResultUiEvent.result.count
                        ),
                        withDismissAction = true
                    )
                }
            }
            null -> {}
        }
        when (downloadResultUiEvent) {
            is DownloadResultUiEvent.SUCCESS -> {
                scope.launch {
                    snackbarHostState.showSnackbar(
                        message = appContext.getString(
                            R.string.webdav_screen_download_success,
                            downloadResultUiEvent.result.time / 1000.0f,
                            downloadResultUiEvent.result.count
                        ),
                        withDismissAction = true
                    )
                }
            }
            null -> {}
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            RacaTopBar(
                style = RacaTopBarStyle.Large,
                scrollBehavior = scrollBehavior,
                title = { Text(text = stringResource(R.string.webdav_screen_name)) },
                navigationIcon = { BackIcon { navController.popBackStack() } },
            )
        }
    ) { paddingValues ->
        var server by remember { mutableStateOf("") }
        var account by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }

        LaunchedEffect(Unit) {
            server = sharedPreferences().getString("webDavServer", null).orEmpty()
            account = secretSharedPreferences().getString("webDavAccount", null).orEmpty()
            password = secretSharedPreferences().getString("webDavPassword", null).orEmpty()
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .nestedScroll(scrollBehavior.nestedScrollConnection), contentPadding = paddingValues
        ) {
            item {
                CategorySettingsItem(
                    text = stringResource(id = R.string.webdav_screen_service_category)
                )
            }
            item {
                BaseSettingsItem(
                    icon = rememberVectorPainter(image = Icons.Default.Dns),
                    text = stringResource(id = R.string.webdav_screen_server),
                    descriptionText = server.ifBlank {
                        stringResource(id = R.string.webdav_screen_server_description)
                    },
                    onClick = {
                        openInputDialog = Triple(
                            appContext.getString(R.string.webdav_screen_input_server), server
                        ) {
                            server = if (!it.endsWith("/")) "$it/" else it
                            openInputDialog = null
                            sharedPreferences().editor { putString("webDavServer", server) }
                        }
                    }
                )
                BaseSettingsItem(
                    icon = rememberVectorPainter(image = Icons.Default.AccountCircle),
                    text = stringResource(id = R.string.webdav_screen_account),
                    descriptionText = account.ifBlank {
                        stringResource(id = R.string.webdav_screen_account_description)
                    },
                    onClick = {
                        openInputDialog = Triple(
                            appContext.getString(R.string.webdav_screen_input_account), account
                        ) {
                            account = it
                            openInputDialog = null
                            secretSharedPreferences().editor { putString("webDavAccount", it) }
                        }
                    }
                )
                BaseSettingsItem(
                    icon = rememberVectorPainter(image = Icons.Default.Key),
                    text = stringResource(id = R.string.webdav_screen_password),
                    descriptionText = stringResource(
                        id = if (password.isBlank()) R.string.webdav_screen_password_description
                        else R.string.webdav_screen_password_entered
                    ),
                    onClick = {
                        openInputDialog = Triple(
                            appContext.getString(R.string.webdav_screen_input_password), password
                        ) {
                            password = it
                            openInputDialog = null
                            secretSharedPreferences().editor { putString("webDavPassword", it) }
                        }
                    }
                )
            }
            item {
                CategorySettingsItem(
                    text = stringResource(id = R.string.webdav_screen_sync_category)
                )
            }
            item {
                BaseSettingsItem(
                    icon = rememberVectorPainter(image = Icons.Default.CloudDownload),
                    text = stringResource(id = R.string.webdav_screen_download),
                    descriptionText = stringResource(id = R.string.webdav_screen_download_description),
                    onClick = {
                        viewModel.sendUiIntent(
                            WebDavIntent.StartDownload(
                                website = server, username = account, password = password
                            )
                        )
                    }
                )
                BaseSettingsItem(
                    icon = rememberVectorPainter(image = Icons.Default.CloudUpload),
                    text = stringResource(id = R.string.webdav_screen_upload),
                    descriptionText = stringResource(id = R.string.webdav_screen_upload_description),
                    onClick = {
                        viewModel.sendUiIntent(
                            WebDavIntent.StartUpload(
                                website = server, username = account, password = password
                            )
                        )
                    }
                )
                BaseSettingsItem(
                    icon = rememberVectorPainter(image = Icons.Default.CloudUpload),
                    text = stringResource(id = R.string.webdav_screen_remote_recycle_bin),
                    descriptionText = stringResource(id = R.string.webdav_screen_remote_recycle_bin_description),
                    onClick = {
                        viewModel.sendUiIntent(
                            WebDavIntent.GetRemoteRecycleBin(
                                website = server, username = account, password = password
                            )
                        )
                        openRecycleBinBottomSheet = true
                    }
                )
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
                }
            }
        }

        if (openWaitingDialog) {
            WaitingDialog()
        }
        openDeleteWarningDialog?.let {
            DeleteWarningDialog(
                onDismissRequest = { openDeleteWarningDialog = null },
                onDismiss = { openDeleteWarningDialog = null },
                onConfirm = {
                    openDeleteWarningDialog = null
                    if (it.isBlank()) {
                        viewModel.sendUiIntent(
                            WebDavIntent.ClearRemoteRecycleBin(
                                website = server, username = account, password = password,
                            )
                        )
                    } else {
                        viewModel.sendUiIntent(
                            WebDavIntent.DeleteFromRemoteRecycleBin(
                                website = server, username = account, password = password, uuid = it
                            )
                        )
                    }
                }
            )
        }
        openInputDialog?.let { pair ->
            InputDialog(
                title = pair.first,
                initText = pair.second,
                onDismissRequest = { openInputDialog = null },
                onClick = pair.third
            )
        }
        if (openRecycleBinBottomSheet) {
            RecycleBinBottomSheet(
                onDismissRequest = { openRecycleBinBottomSheet = false },
                onDelete = { openDeleteWarningDialog = it },
                onClear = { openDeleteWarningDialog = "" }
            )
        }
    }
}

@Composable
private fun WaitingDialog() {
    AlertDialog(
        onDismissRequest = { },
        icon = {
            CircularProgressIndicator()
        },
        title = {
            Text(text = stringResource(R.string.webdav_screen_waiting))
        },
        confirmButton = {}
    )
}

@Composable
private fun InputDialog(
    title: String,
    initText: String = "",
    onDismissRequest: () -> Unit,
    onClick: (String) -> Unit
) {
    var textFieldValue by remember {
        mutableStateOf(TextFieldValue(initText, TextRange(0, initText.length)))
    }
    val focusRequester = remember { FocusRequester() }

    AlertDialog(
        onDismissRequest = onDismissRequest,
        icon = {
            Icon(imageVector = Icons.Default.Input, contentDescription = null)
        },
        title = {
            Text(text = title)
        },
        text = {
            TextField(
                modifier = Modifier.focusRequester(focusRequester),
                value = textFieldValue,
                onValueChange = { textFieldValue = it },
                label = { Text(title) },
                singleLine = true
            )
        },
        confirmButton = {
            TextButton(
                enabled = textFieldValue.text.isNotBlank(),
                onClick = { onClick(textFieldValue.text) }
            ) {
                Text(text = stringResource(id = R.string.dialog_ok))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text(text = stringResource(id = R.string.dialog_cancel))
            }
        }
    )

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }
}

@Composable
private fun RecycleBinBottomSheet(
    onDismissRequest: () -> Unit,
    onDelete: (String) -> Unit,
    onClear: () -> Unit,
    viewModel: WebDavViewModel = hiltViewModel()
) {
    var list: List<BackupInfo>

    viewModel.uiStateFlow.collectAsStateWithLifecycle().value.apply {
        list = when (getRemoteRecycleBinResultUiState) {
            GetRemoteRecycleBinResultUiState.INIT -> emptyList()
            is GetRemoteRecycleBinResultUiState.SUCCESS -> getRemoteRecycleBinResultUiState.result
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
                        Icon(imageVector = Icons.Default.Lightbulb, contentDescription = null)
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
                        IconButton(onClick = { onDelete(list[it].uuid) }) {
                            Icon(
                                Icons.Default.Delete,
                                contentDescription = stringResource(R.string.webdav_screen_delete),
                            )
                        }
                    }
                )
                Divider(modifier = Modifier.padding(horizontal = 16.dp))
            }
        }
    }
}