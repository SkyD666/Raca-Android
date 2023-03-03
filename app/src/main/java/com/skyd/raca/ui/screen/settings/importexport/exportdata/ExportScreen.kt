package com.skyd.raca.ui.screen.settings.importexport.exportdata

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.skyd.raca.R
import com.skyd.raca.appContext
import com.skyd.raca.base.LoadUiIntent
import com.skyd.raca.ui.component.*
import com.skyd.raca.ui.local.LocalNavController
import kotlinx.coroutines.launch

const val EXPORT_SCREEN_ROUTE = "exportScreen"

@Composable
fun ExportScreen(viewModel: ExportDataViewModel = hiltViewModel()) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val navController = LocalNavController.current
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    var openWaitingDialog by remember { mutableStateOf(false) }

    var dirUri by remember { mutableStateOf<Uri?>(null) }
    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            RacaTopBar(
                style = RacaTopBarStyle.Large,
                title = { Text(text = stringResource(R.string.export_screen_name)) },
                scrollBehavior = scrollBehavior,
                navigationIcon = {
                    BackIcon { navController.popBackStack() }
                },
                actions = {
                    IconButton(
                        enabled = dirUri != null,
                        onClick = {
                            val a = dirUri ?: return@IconButton
                            viewModel.sendUiIntent(
                                ExportDataIntent.StartExport(dirUri = a)
                            )
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Done,
                            contentDescription = stringResource(R.string.export_screen_start_export)
                        )
                    }
                }
            )
        }
    ) {
        val pickDirLauncher = rememberLauncherForActivityResult(
            ActivityResultContracts.OpenDocumentTree()
        ) { uri ->
            dirUri = uri
        }
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .nestedScroll(scrollBehavior.nestedScrollConnection),
            contentPadding = it
        ) {
            item {
                CategorySettingsItem(
                    text = stringResource(id = R.string.export_screen_select_dir_category)
                )
            }
            item {
                BaseSettingsItem(
                    icon = rememberVectorPainter(image = Icons.Default.Folder),
                    text = stringResource(id = R.string.export_screen_select_dir),
                    descriptionText = dirUri?.path,
                    onClick = {
                        pickDirLauncher.launch(null)
                    }
                )
            }
        }

        viewModel.loadUiIntentFlow.collectAsState(initial = null).value?.also { loadUiIntent ->
            when (loadUiIntent) {
                is LoadUiIntent.Error -> {
                    scope.launch {
                        snackbarHostState.showSnackbar(
                            message = appContext.getString(
                                R.string.export_screen_failed,
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
        viewModel.uiStateFlow.collectAsState().value.apply {
            when (exportResultUiState) {
                is ExportResultUiState.SUCCESS -> {
                    scope.launch {
                        snackbarHostState.showSnackbar(
                            message = appContext.getString(
                                R.string.export_screen_success,
                                exportResultUiState.time / 1000.0
                            ),
                            withDismissAction = true
                        )
                    }
                }
                ExportResultUiState.INIT -> {}
            }
        }
        if (openWaitingDialog) {
            WaitingDialog()
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
            Text(text = stringResource(R.string.export_screen_waiting))
        },
        confirmButton = {}
    )
}
