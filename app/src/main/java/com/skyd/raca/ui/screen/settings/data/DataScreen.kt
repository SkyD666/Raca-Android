package com.skyd.raca.ui.screen.settings.data

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.ImportExport
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.skyd.raca.R
import com.skyd.raca.appContext
import com.skyd.raca.base.LoadUiIntent
import com.skyd.raca.ui.component.RacaTopBar
import com.skyd.raca.ui.component.RacaTopBarStyle
import com.skyd.raca.ui.component.dialog.DeleteWarningDialog
import com.skyd.raca.ui.component.dialog.WaitingDialog
import com.skyd.raca.ui.local.LocalNavController
import com.skyd.raca.ui.screen.settings.data.importexport.ImportExportRoute
import com.skyd.settings.BaseSettingsItem
import com.skyd.settings.SettingsLazyColumn
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import org.koin.compose.viewmodel.koinViewModel


@Serializable
data object DataRoute

@Composable
fun DataScreen(viewModel: DataViewModel = koinViewModel()) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val navController = LocalNavController.current
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    var openDeleteWarningDialog by rememberSaveable { mutableStateOf(false) }
    var openWaitingDialog by remember { mutableStateOf(false) }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            RacaTopBar(
                style = RacaTopBarStyle.LargeFlexible,
                scrollBehavior = scrollBehavior,
                title = { Text(text = stringResource(R.string.data_screen_name)) },
            )
        }
    ) { paddingValues ->
        SettingsLazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .nestedScroll(scrollBehavior.nestedScrollConnection),
            contentPadding = paddingValues,
        ) {
            group {
                item {
                    BaseSettingsItem(
                        icon = rememberVectorPainter(image = Icons.Outlined.ImportExport),
                        text = stringResource(id = R.string.import_export_screen_name),
                        descriptionText = stringResource(id = R.string.data_screen_import_export_description),
                        onClick = { navController.navigate(ImportExportRoute) }
                    )
                }
                item {
                    BaseSettingsItem(
                        icon = rememberVectorPainter(image = Icons.Outlined.Delete),
                        text = stringResource(id = R.string.data_screen_delete_all),
                        descriptionText = stringResource(id = R.string.data_screen_delete_all_description),
                        onClick = { openDeleteWarningDialog = true }
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
                                R.string.data_screen_failed, loadUiIntent.msg
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
            when (deleteAllResultUiEvent) {
                is DeleteAllResultUiEvent.Success -> {
                    scope.launch {
                        snackbarHostState.showSnackbar(
                            message = appContext.getString(
                                R.string.data_screen_delete_all_success,
                                deleteAllResultUiEvent.time / 1000.0f
                            ),
                            withDismissAction = true
                        )
                    }
                }

                null -> {}
            }
        }

        WaitingDialog(visible = openWaitingDialog)
        DeleteWarningDialog(
            visible = openDeleteWarningDialog,
            onDismissRequest = { openDeleteWarningDialog = false },
            onDismiss = { openDeleteWarningDialog = false },
            onConfirm = {
                viewModel.sendUiIntent(DataIntent.Start)
                openDeleteWarningDialog = false
            }
        )
    }
}
