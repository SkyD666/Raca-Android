package com.skyd.raca.ui.screen.settings.data.importexport.file.exportdata

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Done
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.skyd.raca.R
import com.skyd.raca.appContext
import com.skyd.raca.base.LoadUiIntent
import com.skyd.raca.ui.component.RacaIconButton
import com.skyd.raca.ui.component.RacaTopBar
import com.skyd.raca.ui.component.RacaTopBarStyle
import com.skyd.raca.ui.component.dialog.WaitingDialog
import com.skyd.settings.BaseSettingsItem
import com.skyd.settings.CategorySettingsItem
import com.skyd.settings.SettingsLazyColumn
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import org.koin.compose.viewmodel.koinViewModel

@Serializable
data object ExportRoute

@Composable
fun ExportScreen(viewModel: ExportDataViewModel = koinViewModel()) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    var openWaitingDialog by remember { mutableStateOf(false) }

    var dirUri by remember { mutableStateOf<Uri?>(null) }
    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            RacaTopBar(
                style = RacaTopBarStyle.LargeFlexible,
                title = { Text(text = stringResource(R.string.export_screen_name)) },
                scrollBehavior = scrollBehavior,
                actions = {
                    RacaIconButton(
                        enabled = dirUri != null,
                        imageVector = Icons.Outlined.Done,
                        contentDescription = stringResource(R.string.export_screen_start_export),
                        onClick = {
                            val a = dirUri ?: return@RacaIconButton
                            viewModel.sendUiIntent(
                                ExportDataIntent.StartExport(dirUri = a)
                            )
                        }
                    )
                }
            )
        }
    ) {
        val pickDirLauncher = rememberLauncherForActivityResult(
            ActivityResultContracts.OpenDocumentTree()
        ) { uri ->
            dirUri = uri
        }
        SettingsLazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .nestedScroll(scrollBehavior.nestedScrollConnection),
            contentPadding = it,
        ) {
            group(category = {
                CategorySettingsItem(
                    text = stringResource(id = R.string.export_screen_select_dir_category)
                )
            }) {
                item {
                    BaseSettingsItem(
                        icon = rememberVectorPainter(image = Icons.Outlined.Folder),
                        text = stringResource(id = R.string.export_screen_select_dir),
                        descriptionText = dirUri?.path,
                        onClick = {
                            pickDirLauncher.launch(null)
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
        viewModel.uiEventFlow.collectAsStateWithLifecycle(initialValue = null).value?.apply {
            when (exportResultUiEvent) {
                is ExportResultUiEvent.Success -> {
                    scope.launch {
                        snackbarHostState.showSnackbar(
                            message = appContext.getString(
                                R.string.export_screen_success,
                                exportResultUiEvent.time / 1000.0
                            ),
                            withDismissAction = true
                        )
                    }
                }

                null -> {}
            }
        }
        WaitingDialog(
            visible = openWaitingDialog,
            title = stringResource(R.string.export_screen_waiting)
        )
    }
}
