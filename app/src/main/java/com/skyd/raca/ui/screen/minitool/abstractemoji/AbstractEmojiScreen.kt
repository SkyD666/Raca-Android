package com.skyd.raca.ui.screen.minitool.abstractemoji

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ClipboardManager
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.skyd.raca.R
import com.skyd.raca.base.LoadUiIntent
import com.skyd.raca.ui.component.RacaTopBar
import com.skyd.raca.ui.component.dialog.WaitingDialog
import kotlinx.serialization.Serializable
import org.koin.compose.viewmodel.koinViewModel

@Serializable
data object AbstractEmojiRoute

@Composable
fun AbstractEmojiScreen(viewModel: AbstractEmojiViewModel = koinViewModel()) {
    var openWaitingDialog by remember { mutableStateOf(false) }
    var isInputState by remember { mutableStateOf(true) }
    Scaffold(
        topBar = {
            RacaTopBar(
                title = { Text(text = stringResource(id = R.string.abstract_emoji_screen_name)) },
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            InputArea()
            AnimatedVisibility(
                visible = !isInputState,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
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
private fun ResultArea(viewModel: AbstractEmojiViewModel = koinViewModel()) {
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
    }
}

@Composable
private fun InputArea(viewModel: AbstractEmojiViewModel = koinViewModel()) {
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
        Row(
            modifier = Modifier
                .padding(top = 16.dp)
                .align(Alignment.End)
        ) {
            AnimatedVisibility(
                visible = articleText.isNotEmpty(),
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Button(
                    onClick = { articleText = "" }
                ) {
                    Text(stringResource(R.string.abstract_emoji_screen_reset))
                }
            }
            Spacer(modifier = Modifier.width(16.dp))
            Button(
                onClick = { viewModel.sendUiIntent(AbstractEmojiIntent.Convert(articleText)) }
            ) {
                Text(stringResource(R.string.abstract_emoji_screen_convert))
            }
        }
    }
}