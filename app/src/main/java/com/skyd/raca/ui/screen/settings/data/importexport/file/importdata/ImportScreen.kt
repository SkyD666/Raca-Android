package com.skyd.raca.ui.screen.settings.data.importexport.file.importdata

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Article
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.skyd.raca.R
import com.skyd.raca.appContext
import com.skyd.raca.base.LoadUiIntent
import com.skyd.raca.ui.component.*
import com.skyd.raca.ui.component.dialog.WaitingDialog
import kotlinx.coroutines.launch

const val IMPORT_SCREEN_ROUTE = "importScreen"

@Composable
fun ImportScreen(viewModel: ImportDataViewModel = hiltViewModel()) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    var openWaitingDialog by remember { mutableStateOf(false) }

    var articleUri by remember { mutableStateOf<Uri?>(null) }
    var tagUri by remember { mutableStateOf<Uri?>(null) }
    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            RacaTopBar(
                style = RacaTopBarStyle.Large,
                title = { Text(text = stringResource(R.string.import_screen_name)) },
                scrollBehavior = scrollBehavior,
                actions = {
                    RacaIconButton(
                        imageVector = Icons.Default.Done,
                        contentDescription = stringResource(R.string.import_screen_start_import),
                        enabled = articleUri != null && tagUri != null,
                        onClick = {
                            val a = articleUri
                            val t = tagUri
                            if (a == null || t == null) return@RacaIconButton
                            viewModel.sendUiIntent(
                                ImportDataIntent.StartImport(articleUri = a, tagUri = t)
                            )
                        }
                    )
                }
            )
        }
    ) {
        val pickArticleFileLauncher = rememberLauncherForActivityResult(
            ActivityResultContracts.GetContent()
        ) { fileUri ->
            articleUri = fileUri
        }
        val pickTagFileLauncher = rememberLauncherForActivityResult(
            ActivityResultContracts.GetContent()
        ) { fileUri ->
            tagUri = fileUri
        }
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .nestedScroll(scrollBehavior.nestedScrollConnection),
            contentPadding = it
        ) {
            item {
                CategorySettingsItem(
                    text = stringResource(id = R.string.import_screen_select_file_category)
                )
            }
            item {
                BaseSettingsItem(
                    icon = rememberVectorPainter(image = Icons.Default.Article),
                    text = stringResource(id = R.string.import_screen_select_article_table),
                    descriptionText = articleUri?.path,
                    onClick = {
                        pickArticleFileLauncher.launch("text/*")
                    }
                )
            }
            item {
                BaseSettingsItem(
                    icon = rememberVectorPainter(image = Icons.Default.Article),
                    text = stringResource(id = R.string.import_screen_select_tag_table),
                    descriptionText = tagUri?.path,
                    onClick = {
                        pickTagFileLauncher.launch("text/*")
                    }
                )
            }
            item {
                CategorySettingsItem(
                    text = stringResource(id = R.string.import_screen_strategy_category)
                )
            }
            item {
                BaseSettingsItem(
                    icon = rememberVectorPainter(image = Icons.Default.Warning),
                    text = stringResource(id = R.string.import_screen_conflict_strategy),
                    descriptionText = stringResource(id = R.string.import_screen_conflict_strategy_description),
                    onClick = {}
                )
            }
        }

        viewModel.loadUiIntentFlow.collectAsStateWithLifecycle(initialValue = null).value?.also { loadUiIntent ->
            when (loadUiIntent) {
                is LoadUiIntent.Error -> {
                    scope.launch {
                        snackbarHostState.showSnackbar(
                            message = appContext.getString(R.string.import_screen_failed),
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
            when (importResultUiEvent) {
                is ImportResultUiEvent.Success -> {
                    scope.launch {
                        snackbarHostState.showSnackbar(
                            message = appContext.getString(
                                R.string.import_screen_success,
                                importResultUiEvent.time / 1000.0f
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
            title = stringResource(R.string.import_screen_waiting)
        )
    }
}
