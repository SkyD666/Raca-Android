package com.skyd.raca.ui.screen.settings.data.importexport.file.importdata

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Article
import androidx.compose.material.icons.outlined.Done
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
import com.skyd.settings.TipSettingsItem
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import org.koin.compose.viewmodel.koinViewModel

@Serializable
data object ImportRoute

@Composable
fun ImportScreen(viewModel: ImportDataViewModel = koinViewModel()) {
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
                style = RacaTopBarStyle.LargeFlexible,
                title = { Text(text = stringResource(R.string.import_screen_name)) },
                scrollBehavior = scrollBehavior,
                actions = {
                    RacaIconButton(
                        imageVector = Icons.Outlined.Done,
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
        SettingsLazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .nestedScroll(scrollBehavior.nestedScrollConnection),
            contentPadding = it,
        ) {
            group(category = {
                CategorySettingsItem(text = stringResource(R.string.import_screen_select_file_category))
            }) {
                item {
                    BaseSettingsItem(
                        icon = rememberVectorPainter(image = Icons.AutoMirrored.Outlined.Article),
                        text = stringResource(id = R.string.import_screen_select_article_table),
                        descriptionText = articleUri?.path,
                        onClick = {
                            pickArticleFileLauncher.launch("text/*")
                        }
                    )
                }
                item {
                    BaseSettingsItem(
                        icon = rememberVectorPainter(image = Icons.AutoMirrored.Outlined.Article),
                        text = stringResource(id = R.string.import_screen_select_tag_table),
                        descriptionText = tagUri?.path,
                        onClick = {
                            pickTagFileLauncher.launch("text/*")
                        }
                    )
                }
            }
            group(category = {
                CategorySettingsItem(text = stringResource(R.string.import_screen_strategy_category))
            }) {
                otherItem {
                    TipSettingsItem(
                        text = stringResource(R.string.import_screen_conflict_strategy) + "\n\n"
                                + stringResource(R.string.import_screen_conflict_strategy_description),
                    )
                }
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
