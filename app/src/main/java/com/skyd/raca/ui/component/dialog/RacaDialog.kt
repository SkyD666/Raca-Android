/**
 * Copyright (C) 2023 Ashinch
 *
 * @link https://github.com/Ashinch/ReadYou
 * @author Ashinch
 * @modifier SkyD666
 */
package com.skyd.raca.ui.component.dialog

import androidx.compose.foundation.layout.size
import androidx.compose.material3.AlertDialog
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import com.skyd.raca.R
import com.skyd.raca.ui.component.RacaLottieAnimation

@Composable
fun RacaDialog(
    modifier: Modifier = Modifier,
    visible: Boolean,
    properties: DialogProperties = DialogProperties(),
    onDismissRequest: () -> Unit = {},
    icon: @Composable (() -> Unit)? = {
        RacaLottieAnimation(
            modifier = Modifier.size(48.dp),
            resId = R.raw.lottie_genshin_impact_venti_1
        )
    },
    title: @Composable (() -> Unit)? = null,
    text: @Composable (() -> Unit)? = null,
    confirmButton: @Composable () -> Unit,
    dismissButton: @Composable (() -> Unit)? = null,
) {
    if (visible) {
        AlertDialog(
            properties = properties,
            modifier = modifier,
            onDismissRequest = onDismissRequest,
            icon = icon,
            title = title,
            text = text,
            confirmButton = confirmButton,
            dismissButton = dismissButton,
        )
    }
}