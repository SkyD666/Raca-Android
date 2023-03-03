package com.skyd.raca.ui.screen.add

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.skyd.raca.R
import com.skyd.raca.appContext
import com.skyd.raca.ext.plus
import com.skyd.raca.model.bean.ArticleBean
import com.skyd.raca.model.bean.ArticleWithTags
import com.skyd.raca.model.bean.TagBean
import com.skyd.raca.ui.component.BackIcon
import com.skyd.raca.ui.component.RacaTopBar
import com.skyd.raca.ui.component.TopBarIcon
import com.skyd.raca.ui.local.LocalNavController
import kotlinx.coroutines.launch

const val ADD_SCREEN_ROUTE = "addScreen"

@Composable
fun AddScreen(articleId: Long, article: String, viewModel: AddViewModel = hiltViewModel()) {
    var openDialog by remember { mutableStateOf(false) }
    var dialogMessage by remember { mutableStateOf("") }
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current
    val focusRequester = remember { FocusRequester() }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val navController = LocalNavController.current
    var titleText by rememberSaveable { mutableStateOf("") }
    var articleText by rememberSaveable { mutableStateOf("") }
    val tags = remember { mutableStateListOf<TagBean>() }

    if (articleId != 0L) {
        viewModel.sendUiIntent(AddIntent.GetArticleWithTags(articleId))
    } else {
        articleText = article
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
                            if (articleId == 0L) R.string.add_screen_name
                            else R.string.add_screen_name_edit
                        )
                    )
                },
                navigationIcon = { BackIcon { navController.popBackStack() } },
                actions = {
                    TopBarIcon(
                        imageVector = Icons.Default.Done,
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
                                        .apply { id = articleId },
                                    tags = tags.toList()
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
                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 20.dp),
                    value = articleText,
                    onValueChange = { articleText = it },
                    label = { Text(stringResource(R.string.add_screen_article)) }
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
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = {
                        if (currentTagText.isNotBlank()) {
                            tags.add(TagBean(tag = currentTagText))
                        } else {
                            keyboardController?.hide()
                            focusManager.clearFocus()
                        }
                        currentTagText = ""
                    })
                )
                FlowRow(modifier = Modifier.fillMaxWidth()) {
                    repeat(tags.size) { index ->
                        InputChip(
                            selected = false,
                            modifier = Modifier.padding(horizontal = 4.dp),
                            label = { Text(tags[index].tag) },
                            onClick = { tags.remove(tags[index]) },
                            trailingIcon = {
                                Icon(
                                    modifier = Modifier.size(AssistChipDefaults.IconSize),
                                    imageVector = Icons.Default.Close, contentDescription = null
                                )
                            }
                        )
                    }
                }
            }
        }

        if (openDialog) {
            SuccessDialog(dialogMessage, {
                openDialog = false
                navController.popBackStack()
            }, {
                openDialog = false
                navController.popBackStack()
            })
        }
    }

    viewModel.uiStateFlow.collectAsState().value.apply {
        if (addArticleResultUiState !is AddArticleResultUiState.SUCCESS) {
            when (getArticleWithTagsUiState) {
                is GetArticleWithTagsUiState.SUCCESS -> {
                    val articleBean = getArticleWithTagsUiState.articleWithTags.article
                    titleText = articleBean.title
                    articleText = articleBean.article
                    tags.clear()
                    tags.addAll(getArticleWithTagsUiState.articleWithTags.tags)
                }
                GetArticleWithTagsUiState.FAILED -> {}
                GetArticleWithTagsUiState.INIT -> {}
            }
        }

        when (addArticleResultUiState) {
            AddArticleResultUiState.INIT -> {
            }
            AddArticleResultUiState.FAILED -> {
            }
            is AddArticleResultUiState.SUCCESS -> {
                val savedStateHandle = navController.previousBackStackEntry?.savedStateHandle
                savedStateHandle?.set("articleId", addArticleResultUiState.articleId)
                dialogMessage = stringResource(R.string.add_screen_success)
                openDialog = true
            }
        }
    }
}

@Composable
private fun SuccessDialog(message: String, onDismissRequest: () -> Unit, onConfirm: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        icon = {
            Icon(imageVector = Icons.Default.Info, contentDescription = null)
        },
        title = {
            Text(text = stringResource(R.string.dialog_tip))
        },
        text = {
            Text(text = message)
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(stringResource(R.string.dialog_ok))
            }
        }
    )
}
