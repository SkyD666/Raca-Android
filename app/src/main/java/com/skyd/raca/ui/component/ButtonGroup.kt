package com.skyd.raca.ui.component

import androidx.compose.material3.ButtonGroupDefaults
import androidx.compose.material3.ToggleButtonShapes
import androidx.compose.runtime.Composable
import kotlin.collections.lastIndex

@Composable
fun ButtonGroupDefaults.connectedButtonShapes(
    list: List<*>,
    index: Int,
): ToggleButtonShapes = when (index) {
    0 -> connectedLeadingButtonShapes()
    list.lastIndex -> connectedTrailingButtonShapes()
    else -> connectedMiddleButtonShapes()
}