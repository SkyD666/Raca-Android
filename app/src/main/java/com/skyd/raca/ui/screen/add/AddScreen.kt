package com.skyd.raca.ui.screen.add

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Done
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.InputChip
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavBackStackEntry
import androidx.navigation.navDeepLink
import androidx.navigation.toRoute
import com.skyd.raca.R
import com.skyd.raca.appContext
import com.skyd.raca.config.refreshArticleData
import com.skyd.raca.ext.addIfAny
import com.skyd.raca.ext.plus
import com.skyd.raca.ext.popBackStackWithLifecycle
import com.skyd.raca.model.bean.ArticleBean
import com.skyd.raca.model.bean.ArticleWithTags
import com.skyd.raca.model.bean.TagBean
import com.skyd.raca.ui.component.RacaIconButton
import com.skyd.raca.ui.component.RacaTopBar
import com.skyd.raca.ui.component.dialog.RacaDialog
import com.skyd.raca.ui.local.LocalNavController
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import org.koin.compose.viewmodel.koinViewModel

const val ADD_SCREEN_ROUTE = "addScreen"

@Serializable
data class AddRoute(
    val initArticleUuid: String?,
    val article: String?,
) {
    companion object {
        val deepLinks = listOf(navDeepLink { mimeType = "text/*" })

        @Composable
        fun AddLauncher(entry: NavBackStackEntry) {
            val entry = entry.toRoute<AddRoute>()
            AddScreen(
                initArticleUuid = entry.initArticleUuid,
                article = entry.article,
            )
        }
    }
}

@Composable
fun AddScreen(
    initArticleUuid: String?,
    article: String?,
    viewModel: AddViewModel = koinViewModel(),
) {
    var openDialog by remember { mutableStateOf(false) }
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current
    val focusRequester = remember { FocusRequester() }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val navController = LocalNavController.current
    var titleText by rememberSaveable { mutableStateOf("") }
    var articleText by rememberSaveable { mutableStateOf("") }
    val tags = remember { mutableStateListOf<TagBean>() }
    var articleUuid by remember { mutableStateOf(initArticleUuid) }

    if (!initArticleUuid.isNullOrBlank()) {
        LaunchedEffect(Unit) {
            viewModel.sendUiIntent(AddIntent.GetArticleWithTags(initArticleUuid))
        }
    } else {
        if (!article.isNullOrBlank()) {
            articleText = article
        }
    }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            RacaTopBar(
                title = {
                    Text(
                        text = stringResource(
                            if (articleUuid.isNullOrBlank()) R.string.add_screen_name
                            else R.string.add_screen_name_edit
                        )
                    )
                },
                actions = {
                    RacaIconButton(
                        imageVector = Icons.Outlined.Done,
                        contentDescription = stringResource(R.string.add_screen_add),
                        onClick = {
                            if (articleText.isBlank()) {
                                scope.launch {
                                    snackbarHostState.showSnackbar(
                                        appContext.getString(R.string.add_screen_article_is_blank),
                                        withDismissAction = true
                                    )
                                }
                            } else {
                                keyboardController?.hide()
                                focusManager.clearFocus()
                                val articleWithTags = ArticleWithTags(
                                    article = ArticleBean(title = titleText, article = articleText)
                                        .apply { articleUuid?.let { uuid = it } },
                                    tags = tags.distinct().toList()
                                )
                                viewModel.sendUiIntent(
                                    AddIntent.AddNewArticleWithTags(articleWithTags)
                                )
                            }
                        }
                    )
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(contentPadding = paddingValues + PaddingValues(horizontal = 16.dp)) {
            item {
                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 20.dp)
                        .focusRequester(focusRequester),
                    value = titleText,
                    onValueChange = { titleText = it },
                    label = { Text(stringResource(R.string.add_screen_title)) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = {
                        focusManager.moveFocus(FocusDirection.Next)
                    })
                )
            }
            item {
                var currentTagText by rememberSaveable { mutableStateOf("") }
                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 20.dp),
                    value = currentTagText,
                    onValueChange = { currentTagText = it },
                    placeholder = { Text(text = stringResource(R.string.add_screen_tag_field_hint)) },
                    label = { Text(stringResource(R.string.add_screen_add_tags)) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = {
                        if (currentTagText.isNotBlank()) {
                            tags.addIfAny(TagBean(tag = currentTagText)) { it.tag != currentTagText }
                        } else {
                            keyboardController?.hide()
                            focusManager.clearFocus()
                        }
                        currentTagText = ""
                    })
                )
            }
            item {
                AnimatedVisibility(
                    visible = tags.isNotEmpty(),
                    enter = expandVertically() + fadeIn(),
                    exit = shrinkVertically() + fadeOut(),
                ) {
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(5.dp),
                    ) {
                        repeat(tags.size) { index ->
                            InputChip(
                                selected = false,
                                label = { Text(tags[index].tag) },
                                onClick = { tags.remove(tags[index]) },
                                trailingIcon = {
                                    Icon(
                                        modifier = Modifier.size(AssistChipDefaults.IconSize),
                                        imageVector = Icons.Outlined.Close,
                                        contentDescription = null
                                    )
                                }
                            )
                        }
                    }
                }
            }
            item {
                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = if (tags.isEmpty()) 20.dp else 10.dp),
                    value = articleText,
                    onValueChange = { articleText = it },
                    label = { Text(stringResource(R.string.add_screen_article)) }
                )
            }
        }

        RacaDialog(
            visible = openDialog,
            title = { Text(text = stringResource(R.string.dialog_tip)) },
            text = { Text(text = stringResource(R.string.add_screen_success)) },
            onDismissRequest = {
                openDialog = false
                navController.popBackStackWithLifecycle()
            },
            confirmButton = {
                TextButton(onClick = {
                    openDialog = false
                    navController.popBackStackWithLifecycle()
                }) {
                    Text(text = stringResource(id = R.string.dialog_ok))
                }
            }
        )

        viewModel.uiStateFlow.collectAsStateWithLifecycle().value.apply {
            when (getArticleWithTagsUiState) {
                is GetArticleWithTagsUiState.Success -> {
                    val articleBean = getArticleWithTagsUiState.articleWithTags.article
                    articleUuid = articleBean.uuid
                    titleText = articleBean.title
                    articleText = articleBean.article
                    tags.clear()
                    tags.addAll(getArticleWithTagsUiState.articleWithTags.tags.distinct())
                }

                GetArticleWithTagsUiState.Failed -> {}
                GetArticleWithTagsUiState.Init -> {}
            }
        }
    }

    viewModel.uiEventFlow.collectAsStateWithLifecycle(initialValue = null).value?.apply {
        when (addArticleResultUiEvent) {
            AddArticleResultUiEvent.Duplicate -> {
                scope.launch {
                    snackbarHostState.showSnackbar(
                        appContext.getString(R.string.add_screen_article_duplicate),
                        withDismissAction = true
                    )
                }
            }

            is AddArticleResultUiEvent.Success -> {
                refreshArticleData.tryEmit(Unit)
                openDialog = true
            }

            null -> {}
        }
    }
}
