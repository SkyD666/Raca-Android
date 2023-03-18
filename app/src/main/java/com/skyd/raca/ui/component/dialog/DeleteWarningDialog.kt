package com.skyd.raca.ui.component.dialog

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.skyd.raca.R
import com.skyd.raca.ui.component.dialog.RacaDialog

@Composable
fun DeleteWarningDialog(
    visible: Boolean,
    onDismissRequest: () -> Unit,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    RacaDialog(
        visible = visible,
        onDismissRequest = onDismissRequest,
        icon = {
            Icon(imageVector = Icons.Default.Warning, contentDescription = null)
        },
        title = {
            Text(text = stringResource(R.string.dialog_warning))
        },
        text = {
            Text(text = stringResource(R.string.home_screen_delete_warning))
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(stringResource(R.string.dialog_ok))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.dialog_cancel))
            }
        }
    )
}