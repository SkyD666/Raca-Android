package com.skyd.raca.ui.component

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue

@Composable
fun suspendString(block: suspend () -> String): String {
    return suspendString(block) { block() }
}

@Composable
fun <T : Any> suspendString(value: T, block: suspend (T) -> String): String {
    var str by rememberSaveable { mutableStateOf("") }
    LaunchedEffect(value, block) {
        str = block(value)
    }
    return str
}