package com.skyd.raca.ui.screen.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.basicMarquee
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
import com.skyd.raca.R
import com.skyd.raca.appContext
import com.skyd.raca.base.LoadUiIntent
import com.skyd.raca.config.refreshArticleData
import com.skyd.raca.ext.screenIsLand
import com.skyd.raca.model.bean.ArticleWithTags
import com.skyd.raca.model.bean.ArticleWithTags1
import com.skyd.raca.model.preference.CurrentArticleUuidPreference
import com.skyd.raca.model.preference.QueryPreference
import com.skyd.raca.ui.component.AnimatedPlaceholder
import com.skyd.raca.ui.component.RacaIconButton
import com.skyd.raca.ui.component.dialog.DeleteWarningDialog
import com.skyd.raca.ui.component.lazyverticalgrid.RacaLazyVerticalGrid
import com.skyd.raca.ui.component.lazyverticalgrid.adapter.LazyGridAdapter
import com.skyd.raca.ui.component.lazyverticalgrid.adapter.proxy.ArticleWithTags1Proxy
import com.skyd.raca.ui.local.LocalCurrentArticleUuid
import com.skyd.raca.ui.local.LocalNavController
import com.skyd.raca.ui.local.LocalQuery
import com.skyd.raca.ui.screen.add.ADD_SCREEN_ROUTE
import com.skyd.raca.ui.screen.settings.searchconfig.SEARCH_CONFIG_SCREEN_ROUTE
import kotlinx.coroutines.launch
import kotlin.random.Random

private var openDeleteWarningDialog by mutableStateOf(false)

@Composable
fun HomeScreen(viewModel: HomeViewModel = hiltViewModel()) {
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    val currentArticleUuid = LocalCurrentArticleUuid.current
    val initQuery = LocalQuery.current
    var query by remember(initQuery) { mutableStateOf(initQuery) }
    var articleWithTags by remember { mutableStateOf<ArticleWithTags?>(null) }

    refreshArticleData.collectAsStateWithLifecycle(initialValue = null).apply {
        value ?: return@apply
        viewModel.sendUiIntent(HomeIntent.GetArticleWithTagsList(query))
        viewModel.sendUiIntent(HomeIntent.GetArticleDetails(currentArticleUuid))
    }

    LaunchedEffect(query) {
        viewModel.sendUiIntent(HomeIntent.GetArticleWithTagsList(query))
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

        Column(
            modifier = Modifier
                .padding(innerPaddings)
                .fillMaxSize()
        ) {
            RacaSearchBar(query = query, onQueryChange = { query = it })

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
                    is ArticleDetailUiState.Init -> {
                        articleWithTags = null
                        AnimatedPlaceholder(
                            resId = R.raw.lottie_genshin_impact_klee_1,
                            tip = stringResource(id = R.string.home_screen_empty_tip)
                        )
                        if (articleDetailUiState.articleUuid.isNotBlank()) {
                            viewModel.sendUiIntent(
                                HomeIntent.GetArticleDetails(articleDetailUiState.articleUuid)
                            )
                        }
                    }
                    is ArticleDetailUiState.Success -> {
                        articleWithTags = articleDetailUiState.articleWithTags
                    }
                }
            }
        }

        viewModel.loadUiIntentFlow.collectAsStateWithLifecycle(initialValue = null).value?.also { loadUiIntent ->
            when (loadUiIntent) {
                is LoadUiIntent.Error -> {
                    scope.launch {
                        snackbarHostState.showSnackbar(
                            message = appContext.getString(
                                R.string.home_screen_failed, loadUiIntent.msg
                            ),
                            withDismissAction = true
                        )
                    }
                }
                is LoadUiIntent.Loading -> {}
                LoadUiIntent.ShowMainView -> {}
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
private fun RacaSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    var menuExpanded by rememberSaveable { mutableStateOf(false) }
    val context = LocalContext.current
    val navController = LocalNavController.current
    val scope = rememberCoroutineScope()
    var active by rememberSaveable { mutableStateOf(false) }
    val keyboardController = LocalSoftwareKeyboardController.current
    val searchBarHorizontalPadding: Dp by animateDpAsState(if (active) 0.dp else 16.dp)

    Box(
        Modifier
            .semantics { isContainer = true }
            .zIndex(1f)
            .fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(horizontal = searchBarHorizontalPadding)
        ) {
            SearchBar(
                onQueryChange = onQueryChange,
                query = query,
                onSearch = { keyword ->
                    keyboardController?.hide()
                    QueryPreference.put(context, scope, keyword)
                    viewModel.sendUiIntent(HomeIntent.GetArticleWithTagsList(keyword))
                },
                active = active,
                onActiveChange = {
                    if (!it) {
                        QueryPreference.put(context, scope, query)
                    }
                    active = it
                },
                placeholder = { Text(text = stringResource(R.string.home_screen_search_hint)) },
                leadingIcon = {
                    if (active) {
                        RacaIconButton(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = stringResource(id = R.string.home_screen_close_search),
                            onClick = { active = false }
                        )
                    } else {
                        RacaIconButton(
                            imageVector = Icons.Default.Menu,
                            contentDescription = stringResource(id = R.string.home_screen_open_menu),
                            onClick = { menuExpanded = true }
                        )
                    }
                },
                trailingIcon = {
                    if (active) {
                        TrailingIcon(showClearButton = query.isNotEmpty()) {
                            onQueryChange(QueryPreference.default)
                        }
                    } else {
                        RacaIconButton(
                            imageVector = Icons.Default.Add,
                            contentDescription = stringResource(R.string.home_screen_add),
                            onClick = { navController.navigate(ADD_SCREEN_ROUTE) }
                        )
                    }
                },
            ) {
                viewModel.uiStateFlow.collectAsStateWithLifecycle().value.apply {
                    when (searchResultUiState) {
                        SearchResultUiState.Init -> {}
                        is SearchResultUiState.Success -> {
                            SearchResultList(dataList = searchResultUiState.articleWithTagsList,
                                onItemClickListener = {
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
            HomeMenu(expanded = menuExpanded, onDismissRequest = { menuExpanded = false })
        }
    }
}

@Composable
private fun TrailingIcon(
    showClearButton: Boolean = true,
    onClick: (() -> Unit)? = null
) {
    if (showClearButton) {
        RacaIconButton(
            imageVector = Icons.Default.Clear,
            contentDescription = stringResource(R.string.home_screen_clear_search_text),
            onClick = { onClick?.invoke() }
        )
    }
}

@Composable
private fun SearchResultList(
    dataList: List<Any>,
    onItemClickListener: ((data: ArticleWithTags1) -> Unit)? = null
) {
    Box {
        if (dataList.isEmpty()) {
            val resId = remember {
                arrayOf(
                    R.raw.lottie_genshin_impact_klee_2,
                    R.raw.lottie_genshin_impact_diona_1
                )[Random.nextInt(2)]
            }
            AnimatedPlaceholder(
                resId = resId,
                tip = stringResource(id = R.string.home_screen_no_search_result_tip)
            )
        } else {
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

        Badge(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 10.dp, end = 10.dp),
        ) {
            Text(text = dataList.size.toString())
        }
    }
}

@Composable
private fun HomeMenu(
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val navController = LocalNavController.current
    val currentArticleUuid = LocalCurrentArticleUuid.current
    var editMenuItemEnabled by remember { mutableStateOf(false) }
    var deleteMenuItemEnabled by remember { mutableStateOf(false) }

    viewModel.uiStateFlow.collectAsStateWithLifecycle().value.apply {
        if (articleDetailUiState is ArticleDetailUiState.Success) {
            editMenuItemEnabled = true
            deleteMenuItemEnabled = true
        } else {
            editMenuItemEnabled = false
            deleteMenuItemEnabled = false
        }
    }

    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismissRequest,
    ) {
        DropdownMenuItem(
            enabled = editMenuItemEnabled,
            text = { Text(stringResource(R.string.home_screen_clear_current_article)) },
            onClick = {
                viewModel.sendUiIntent(
                    HomeIntent.GetArticleDetails(CurrentArticleUuidPreference.default)
                )
                onDismissRequest()
            },
            leadingIcon = {
                Icon(
                    Icons.Default.Replay,
                    contentDescription = null
                )
            }
        )
        DropdownMenuItem(
            enabled = editMenuItemEnabled,
            text = { Text(stringResource(R.string.home_screen_edit)) },
            onClick = {
                navController.navigate("$ADD_SCREEN_ROUTE?articleUuid=${currentArticleUuid}")
                onDismissRequest()
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
                onDismissRequest()
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
                onDismissRequest()
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
                        .padding(top = 16.dp)
                        .basicMarquee(iterations = Int.MAX_VALUE),
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
                    horizontalArrangement = Arrangement.spacedBy(5.dp)
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
