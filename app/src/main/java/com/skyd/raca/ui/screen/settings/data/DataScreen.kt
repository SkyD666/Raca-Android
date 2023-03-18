package com.skyd.raca.ui.screen.settings.data

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ImportExport
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.skyd.raca.R
import com.skyd.raca.appContext
import com.skyd.raca.base.LoadUiIntent
import com.skyd.raca.ui.component.BaseSettingsItem
import com.skyd.raca.ui.component.RacaTopBar
import com.skyd.raca.ui.component.RacaTopBarStyle
import com.skyd.raca.ui.component.dialog.DeleteWarningDialog
import com.skyd.raca.ui.component.dialog.WaitingDialog
import com.skyd.raca.ui.local.LocalNavController
import com.skyd.raca.ui.screen.settings.data.importexport.IMPORT_EXPORT_SCREEN_ROUTE
import kotlinx.coroutines.launch

const val DATA_SCREEN_ROUTE = "dataScreen"

@Composable
fun DataScreen(viewModel: DataViewModel = hiltViewModel()) {
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
                style = RacaTopBarStyle.Large,
                scrollBehavior = scrollBehavior,
                title = { Text(text = stringResource(R.string.data_screen_name)) },
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .nestedScroll(scrollBehavior.nestedScrollConnection), contentPadding = paddingValues
        ) {
            item {
                BaseSettingsItem(
                    icon = rememberVectorPainter(image = Icons.Default.ImportExport),
                    text = stringResource(id = R.string.import_export_screen_name),
                    descriptionText = stringResource(id = R.string.data_screen_import_export_description),
                    onClick = { navController.navigate(IMPORT_EXPORT_SCREEN_ROUTE) }
                )
                BaseSettingsItem(
                    icon = rememberVectorPainter(image = Icons.Default.Delete),
                    text = stringResource(id = R.string.data_screen_delete_all),
                    descriptionText = stringResource(id = R.string.data_screen_delete_all_description),
                    onClick = { openDeleteWarningDialog = true }
                )
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
                is DeleteAllResultUiEvent.SUCCESS -> {
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
