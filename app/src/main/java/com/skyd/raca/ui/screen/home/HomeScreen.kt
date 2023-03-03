package com.skyd.raca.ui.screen.home

import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.*
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.isContainer
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.flowlayout.FlowRow
import com.skyd.raca.R
import com.skyd.raca.appContext
import com.skyd.raca.config.currentArticleId
import com.skyd.raca.ext.screenIsLand
import com.skyd.raca.model.bean.ArticleWithTags
import com.skyd.raca.model.bean.ArticleWithTags1
import com.skyd.raca.ui.component.lazyverticalgrid.RacaLazyVerticalGrid
import com.skyd.raca.ui.component.lazyverticalgrid.adapter.LazyGridAdapter
import com.skyd.raca.ui.component.lazyverticalgrid.adapter.proxy.ArticleWithTags1Proxy
import com.skyd.raca.ui.local.LocalNavController
import com.skyd.raca.ui.screen.add.ADD_SCREEN_ROUTE
import com.skyd.raca.ui.screen.settings.searchconfig.SEARCH_CONFIG_SCREEN_ROUTE
import kotlinx.coroutines.launch

var menuExpanded by mutableStateOf(false)
var openDeleteWarningDialog by mutableStateOf(false)

@Composable
fun HomeScreen(viewModel: HomeViewModel = hiltViewModel()) {
    val navController = LocalNavController.current
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    viewModel.uiStateFlow.collectAsState().value.apply {
        currentArticleId = when (articleDetailUiState) {
            is ArticleDetailUiState.SUCCESS -> {
                articleDetailUiState.articleWithTags.article.id
            }
            is ArticleDetailUiState.INIT -> {
                articleDetailUiState.articleId
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        contentWindowInsets = if (context.screenIsLand) {
            WindowInsets(
                left = 0,
                top = 0,
                right = ScaffoldDefaults.contentWindowInsets
                    .getRight(LocalDensity.current, LocalLayoutDirection.current),
                bottom = 0
            )
        } else {
            WindowInsets(0.dp)
        }
    ) { innerPaddings ->
        var active by rememberSaveable { mutableStateOf(false) }
        val focusManager = LocalFocusManager.current

        Column(
            modifier = Modifier
                .padding(innerPaddings)
                .fillMaxSize()
        ) {
            Box(
                Modifier
                    .semantics { isContainer = true }
                    .zIndex(1f)
                    .fillMaxWidth()
            ) {
                var query by remember { mutableStateOf("") }
                SearchBar(
                    modifier = Modifier.align(Alignment.TopCenter).let {
                        return@let if (active) it
                        else it.padding(horizontal = 16.dp)
                    },
                    onQueryChange = { query = it },
                    query = query,
                    onSearch = { keyword ->
                        viewModel.sendUiIntent(HomeIntent.GetArticleWithTagsList(keyword))
                        focusManager.clearFocus()
                    },
                    active = active,
                    onActiveChange = {
                        active = it
                        if (!active) focusManager.clearFocus()
                    },
                    placeholder = { Text(text = stringResource(R.string.home_screen_search_hint)) },
                    leadingIcon = {
                        if (active) {
                            IconButton(onClick = {
                                focusManager.clearFocus()
                                active = false
                            }) {
                                Icon(
                                    Icons.Default.ArrowBack,
                                    contentDescription = stringResource(id = R.string.home_screen_close_search)
                                )
                            }
                        } else {
                            IconButton(onClick = { menuExpanded = true }) {
                                Icon(Icons.Default.Menu, contentDescription = null)
                            }
                        }
                    },
                    trailingIcon = {
                        val savedStateHandle = navController.currentBackStackEntry?.savedStateHandle
                        val articleIdState = savedStateHandle
                            ?.getStateFlow<Long>("articleId", 0)
                            ?.collectAsState()
                        articleIdState?.value?.let {
                            savedStateHandle.remove<Long>("articleId")
                            viewModel.sendUiIntent(HomeIntent.GetArticleDetails(it))
                            viewModel.sendUiIntent(HomeIntent.GetArticleWithTagsList(query))
                        }
                        if (active) {
                            IconButton(onClick = { query = "" }) {
                                Icon(
                                    Icons.Default.Clear,
                                    stringResource(R.string.home_screen_clear_search_text)
                                )
                            }
                        } else {
                            IconButton(onClick = {
                                navController.navigate(ADD_SCREEN_ROUTE)
                            }) {
                                Icon(Icons.Default.Add, stringResource(R.string.home_screen_add))
                            }
                        }
                    },
                ) {
                    viewModel.uiStateFlow.collectAsState().value.apply {
                        when (searchResultUiState) {
                            SearchResultUiState.INIT -> {

                            }
                            is SearchResultUiState.SUCCESS -> {
                                SearchResultList(dataList = searchResultUiState.articleWithTagsList,
                                    onItemClickListener = {
                                        focusManager.clearFocus()
                                        active = false
                                        viewModel.sendUiIntent(
                                            HomeIntent.GetArticleDetails(it.article.id)
                                        )
                                    }
                                )
                            }
                        }
                    }
                }
                HomeMenu()
            }

            viewModel.uiStateFlow.collectAsState().value.apply {
                when (articleDetailUiState) {
                    is ArticleDetailUiState.INIT -> {
                        viewModel.sendUiIntent(
                            HomeIntent.GetArticleDetails(articleDetailUiState.articleId)
                        )
                    }
                    is ArticleDetailUiState.SUCCESS -> {
                        MainCard(
                            articleWithTags = articleDetailUiState.articleWithTags,
                            snackbarHostState = snackbarHostState
                        )
                    }
                }
            }
        }

        if (openDeleteWarningDialog && currentArticleId != 0L) {
            DeleteWarningDialog(
                { openDeleteWarningDialog = false },
                { openDeleteWarningDialog = false },
                {
                    openDeleteWarningDialog = false
                    viewModel.sendUiIntent(HomeIntent.DeleteArticleWithTags(currentArticleId))
                }
            )
        }
    }
}

@Composable
private fun SearchResultList(
    dataList: List<Any>,
    onItemClickListener: ((data: ArticleWithTags1) -> Unit)? = null
) {
    val adapter = remember {
        LazyGridAdapter(
            mutableListOf(
                ArticleWithTags1Proxy(onClickListener = onItemClickListener)
            )
        )
    }
    RacaLazyVerticalGrid(
        modifier = Modifier.fillMaxSize(),
        dataList = dataList,
        adapter = adapter,
        contentPadding = PaddingValues(vertical = 7.dp)
    )
}

@Composable
private fun HomeMenu(viewModel: HomeViewModel = hiltViewModel()) {
    val navController = LocalNavController.current
    var editMenuItemEnabled by remember { mutableStateOf(false) }
    var deleteMenuItemEnabled by remember { mutableStateOf(false) }

    viewModel.uiStateFlow.collectAsState().value.apply {
        if (articleDetailUiState is ArticleDetailUiState.SUCCESS) {
            editMenuItemEnabled = true
            deleteMenuItemEnabled = true
        } else {
            editMenuItemEnabled = false
            deleteMenuItemEnabled = false
        }
    }

    DropdownMenu(
        expanded = menuExpanded,
        onDismissRequest = { menuExpanded = false }
    ) {
        DropdownMenuItem(
            enabled = editMenuItemEnabled,
            text = { Text(stringResource(R.string.home_screen_edit)) },
            onClick = {
                menuExpanded = false
                navController.navigate("$ADD_SCREEN_ROUTE?articleId=${currentArticleId}")
            },
            leadingIcon = {
                Icon(
                    Icons.Default.Edit,
                    contentDescription = null
                )
            }
        )
        DropdownMenuItem(
            enabled = deleteMenuItemEnabled,
            text = { Text(stringResource(R.string.home_screen_delete)) },
            onClick = {
                menuExpanded = false
                openDeleteWarningDialog = true
            },
            leadingIcon = {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = null
                )
            }
        )
        Divider()
        DropdownMenuItem(
            text = { Text(stringResource(R.string.search_config_screen_name)) },
            onClick = {
                menuExpanded = false
                navController.navigate(SEARCH_CONFIG_SCREEN_ROUTE)
            },
            leadingIcon = {
                Icon(
                    Icons.Default.ManageSearch,
                    contentDescription = null
                )
            }
        )
    }
}

@Composable
private fun MainCard(articleWithTags: ArticleWithTags, snackbarHostState: SnackbarHostState) {
    val navController = LocalNavController.current
    val clipboardManager: ClipboardManager = LocalClipboardManager.current
    val scope = rememberCoroutineScope()

    val articleBean = articleWithTags.article
    val tags = articleWithTags.tags

    Card(
        modifier = Modifier
            .padding(vertical = 16.dp, horizontal = 16.dp)
            .fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .combinedClickable(
                    onLongClick = {
                        clipboardManager.setText(AnnotatedString(articleBean.article))
                        scope.launch {
                            snackbarHostState.showSnackbar(
                                message = appContext.getString(R.string.add_screen_copy_article_success),
                                withDismissAction = true
                            )
                        }
                    },
                    onDoubleClick = {
                        navController.navigate("$ADD_SCREEN_ROUTE?articleId=${currentArticleId}")
                    },
                    onClick = {}
                )
        ) {
            if (articleBean.title.isNotBlank()) {
                Text(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .padding(top = 16.dp),
                    text = articleBean.title,
                    style = MaterialTheme.typography.titleLarge
                )
            }
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 16.dp)
                    .verticalScroll(rememberScrollState())
                    .weight(weight = 1f, fill = false),
                text = articleBean.article,
                style = MaterialTheme.typography.bodyLarge
            )
            if (tags.isNotEmpty()) {
                Divider()
                FlowRow(
                    modifier = Modifier
                        .padding(vertical = 6.dp, horizontal = 16.dp)
                        .fillMaxWidth(),
                    mainAxisSpacing = 5.dp,
                ) {
                    repeat(tags.size) { index ->
                        AssistChip(
                            onClick = { clipboardManager.setText(AnnotatedString(tags[index].tag)) },
                            label = { Text(tags[index].tag) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DeleteWarningDialog(
    onDismissRequest: () -> Unit,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        icon = {
            Icon(imageVector = Icons.Default.Warning, contentDescription = null)
        },
        title = {
            Text(text = stringResource(R.string.dialog_warning))
        },
        text = {
            Text(text = stringResource(R.string.home_screen_delete_warning))
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(stringResource(R.string.dialog_ok))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.dialog_cancel))
            }
        }
    )
}
