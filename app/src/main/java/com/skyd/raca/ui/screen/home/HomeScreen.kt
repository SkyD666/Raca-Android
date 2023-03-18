package com.skyd.raca.ui.screen.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.accompanist.flowlayout.FlowRow
import com.skyd.raca.R
import com.skyd.raca.appContext
import com.skyd.raca.config.refreshArticleData
import com.skyd.raca.ext.screenIsLand
import com.skyd.raca.model.bean.ArticleWithTags
import com.skyd.raca.model.bean.ArticleWithTags1
import com.skyd.raca.model.preference.QueryPreference
import com.skyd.raca.model.preference.rememberQuery
import com.skyd.raca.ui.component.dialog.DeleteWarningDialog
import com.skyd.raca.ui.component.lazyverticalgrid.RacaLazyVerticalGrid
import com.skyd.raca.ui.component.lazyverticalgrid.adapter.LazyGridAdapter
import com.skyd.raca.ui.component.lazyverticalgrid.adapter.proxy.ArticleWithTags1Proxy
import com.skyd.raca.ui.local.LocalCurrentArticleUuid
import com.skyd.raca.ui.local.LocalNavController
import com.skyd.raca.ui.screen.add.ADD_SCREEN_ROUTE
import com.skyd.raca.ui.screen.settings.searchconfig.SEARCH_CONFIG_SCREEN_ROUTE
import kotlinx.coroutines.launch

private var menuExpanded by mutableStateOf(false)
private var openDeleteWarningDialog by mutableStateOf(false)

@Composable
fun HomeScreen(viewModel: HomeViewModel = hiltViewModel()) {
    val navController = LocalNavController.current
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    val currentArticleUuid = LocalCurrentArticleUuid.current
    var query by rememberQuery()
    var articleWithTags by remember { mutableStateOf<ArticleWithTags?>(null) }

    refreshArticleData.collectAsStateWithLifecycle(initialValue = null).apply {
        value ?: return@apply
        viewModel.sendUiIntent(HomeIntent.GetArticleWithTagsList(query))
        viewModel.sendUiIntent(HomeIntent.GetArticleDetails(currentArticleUuid))
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
        val keyboardController = LocalSoftwareKeyboardController.current

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
                val searchBarHorizontalPadding: Dp by animateDpAsState(if (active) 0.dp else 16.dp)
                Box(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(horizontal = searchBarHorizontalPadding)
                ) {
                    SearchBar(
                        onQueryChange = { query = it },
                        query = query,
                        onSearch = { keyword ->
                            focusManager.clearFocus()
                            keyboardController?.hide()
                            QueryPreference.put(context, scope, keyword)
                            viewModel.sendUiIntent(HomeIntent.GetArticleWithTagsList(keyword))
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
                            if (active) {
                                IconButton(onClick = {
                                    QueryPreference.put(context, scope, QueryPreference.default)
                                }) {
                                    Icon(
                                        imageVector = Icons.Default.Clear,
                                        contentDescription = stringResource(R.string.home_screen_clear_search_text)
                                    )
                                }
                            } else {
                                IconButton(onClick = {
                                    navController.navigate(ADD_SCREEN_ROUTE)
                                }) {
                                    Icon(
                                        imageVector = Icons.Default.Add,
                                        contentDescription = stringResource(R.string.home_screen_add)
                                    )
                                }
                            }
                        },
                    ) {
                        viewModel.uiStateFlow.collectAsStateWithLifecycle().value.apply {
                            when (searchResultUiState) {
                                SearchResultUiState.INIT -> {
                                    viewModel.sendUiIntent(HomeIntent.GetArticleWithTagsList(query))
                                }
                                is SearchResultUiState.SUCCESS -> {
                                    SearchResultList(dataList = searchResultUiState.articleWithTagsList,
                                        onItemClickListener = {
                                            focusManager.clearFocus()
                                            active = false
                                            viewModel.sendUiIntent(
                                                HomeIntent.GetArticleDetails(it.article.uuid)
                                            )
                                        }
                                    )
                                }
                            }
                        }
                    }
                    HomeMenu(viewModel = viewModel)
                }
            }

            AnimatedVisibility(
                visible = articleWithTags != null,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                articleWithTags?.let {
                    MainCard(
                        articleWithTags = it,
                        snackbarHostState = snackbarHostState
                    )
                }
            }

            viewModel.uiStateFlow.collectAsStateWithLifecycle().value.apply {
                when (articleDetailUiState) {
                    is ArticleDetailUiState.INIT -> {
                        articleWithTags = null
                        if (articleDetailUiState.articleUuid.isNotBlank()) {
                            viewModel.sendUiIntent(
                                HomeIntent.GetArticleDetails(articleDetailUiState.articleUuid)
                            )
                        }
                    }
                    is ArticleDetailUiState.SUCCESS -> {
                        articleWithTags = articleDetailUiState.articleWithTags
                    }
                }
            }
        }

        if (openDeleteWarningDialog && currentArticleUuid.isNotBlank()) {
            DeleteWarningDialog(
                visible = openDeleteWarningDialog,
                { openDeleteWarningDialog = false },
                { openDeleteWarningDialog = false },
                {
                    openDeleteWarningDialog = false
                    viewModel.sendUiIntent(HomeIntent.DeleteArticleWithTags(currentArticleUuid))
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
private fun HomeMenu(viewModel: HomeViewModel) {
    val navController = LocalNavController.current
    val currentArticleUuid = LocalCurrentArticleUuid.current
    var editMenuItemEnabled by remember { mutableStateOf(false) }
    var deleteMenuItemEnabled by remember { mutableStateOf(false) }

    viewModel.uiStateFlow.collectAsStateWithLifecycle().value.apply {
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
                navController.navigate("$ADD_SCREEN_ROUTE?articleUuid=${currentArticleUuid}")
                menuExpanded = false
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
                navController.navigate(SEARCH_CONFIG_SCREEN_ROUTE)
                menuExpanded = false
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
    val currentArticleUuid = LocalCurrentArticleUuid.current
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
                        navController.navigate("$ADD_SCREEN_ROUTE?articleUuid=${currentArticleUuid}")
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
                    .padding(horizontal = 16.dp)
                    .padding(top = 16.dp, bottom = if (tags.isEmpty()) 16.dp else 6.dp)
                    .verticalScroll(rememberScrollState())
                    .weight(weight = 1f, fill = false),
                text = articleBean.article,
                style = MaterialTheme.typography.bodyLarge
            )
            if (tags.isNotEmpty()) {
                FlowRow(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .padding(bottom = 6.dp)
                        .fillMaxWidth()
                        .heightIn(max = 150.dp)
                        .verticalScroll(rememberScrollState()),
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
