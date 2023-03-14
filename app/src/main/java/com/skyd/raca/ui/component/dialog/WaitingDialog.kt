package com.skyd.raca.ui.component.dialog

import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.skyd.raca.R

@Composable
fun WaitingDialog(visible: Boolean, text: String = stringResource(R.string.webdav_screen_waiting)) {
    RacaDialog(
        visible = visible,
        onDismissRequest = { },
        icon = {
            CircularProgressIndicator()
        },
        title = {
            Text(text = text)
        },
        confirmButton = {}
    )
}