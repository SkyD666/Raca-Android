package com.skyd.raca.ui.screen.minitool.abstractemoji

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ClipboardManager
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.skyd.raca.R
import com.skyd.raca.base.LoadUiIntent
import com.skyd.raca.ext.popBackStackWithLifecycle
import com.skyd.raca.ui.component.BackIcon
import com.skyd.raca.ui.component.RacaTopBar
import com.skyd.raca.ui.component.dialog.WaitingDialog
import com.skyd.raca.ui.local.LocalNavController

const val ABSTRACT_EMOJI_SCREEN_ROUTE = "abstractEmojiScreen"

@Composable
fun AbstractEmojiScreen(viewModel: AbstractEmojiViewModel = hiltViewModel()) {
    var openWaitingDialog by remember { mutableStateOf(false) }
    var isInputState by remember { mutableStateOf(true) }
    val navController = LocalNavController.current
    BackHandler(enabled = !isInputState) {
        viewModel.sendUiIntent(AbstractEmojiIntent.Reset)
    }
    Scaffold(
        topBar = {
            RacaTopBar(
                title = { Text(text = stringResource(id = R.string.abstract_emoji_screen_name)) },
                navigationIcon = {
                    BackIcon {
                        if (isInputState) navController.popBackStackWithLifecycle()
                        else viewModel.sendUiIntent(AbstractEmojiIntent.Reset)
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            AnimatedVisibility(visible = isInputState) {
                InputArea()
            }
            AnimatedVisibility(visible = !isInputState) {
                ResultArea()
            }
        }
        viewModel.uiStateFlow.collectAsStateWithLifecycle().value.apply {
            isInputState = when (abstractEmojiResultUiState) {
                AbstractEmojiResultUiState.Init -> true
                is AbstractEmojiResultUiState.Success -> false
            }
        }
        viewModel.loadUiIntentFlow.collectAsStateWithLifecycle(initialValue = null).value?.also { loadUiIntent ->
            when (loadUiIntent) {
                is LoadUiIntent.Error -> {}
                is LoadUiIntent.ShowMainView -> {}
                is LoadUiIntent.Loading -> {
                    openWaitingDialog = loadUiIntent.isShow
                }
            }
        }
        WaitingDialog(visible = openWaitingDialog)
    }
}

@Composable
private fun ResultArea(viewModel: AbstractEmojiViewModel = hiltViewModel()) {
    val clipboardManager: ClipboardManager = LocalClipboardManager.current
    var abstractArticle = ""

    viewModel.uiStateFlow.collectAsStateWithLifecycle().value.apply {
        if (abstractEmojiResultUiState is AbstractEmojiResultUiState.Success) {
            abstractArticle = abstractEmojiResultUiState.abstractEmoji
        }
    }
    Card(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth(),
        onClick = {
            clipboardManager.setText(AnnotatedString(abstractArticle))
        }
    ) {
        Text(
            modifier = Modifier.padding(16.dp),
            text = abstractArticle
        )
        Button(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .padding(bottom = 16.dp)
                .align(Alignment.End),
            onClick = { viewModel.sendUiIntent(AbstractEmojiIntent.Reset) }
        ) {
            Text(stringResource(R.string.abstract_emoji_screen_reset))
        }
    }
}

@Composable
private fun InputArea(viewModel: AbstractEmojiViewModel = hiltViewModel()) {
    var articleText by rememberSaveable { mutableStateOf("") }
    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth(),
    ) {
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = articleText,
            onValueChange = { articleText = it },
            label = { Text(stringResource(R.string.abstract_emoji_screen_article)) }
        )
        Button(
            modifier = Modifier
                .padding(top = 16.dp)
                .align(Alignment.End),
            onClick = { viewModel.sendUiIntent(AbstractEmojiIntent.Convert(articleText)) }
        ) {
            Text(stringResource(R.string.abstract_emoji_screen_convert))
        }
    }
}