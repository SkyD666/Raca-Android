package com.skyd.raca.ext

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.flowWithLifecycle
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.shareIn

@SuppressLint("ComposableNaming")
@Composable
fun <T> StateFlow<T>.collectAsSharedState(block: T.() -> Unit) {
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    LaunchedEffect(this) {
        flowWithLifecycle(lifecycle).shareIn(this, SharingStarted.WhileSubscribed()).collect {
            block(it)
        }
    }
}