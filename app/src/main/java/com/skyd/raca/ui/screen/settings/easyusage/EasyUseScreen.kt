package com.skyd.raca.ui.screen.settings.easyusage

import android.content.Intent
import android.os.Build
import android.provider.Settings.ACTION_REQUEST_SET_AUTOFILL_SERVICE
import android.view.autofill.AutofillManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.EditNote
import androidx.compose.material.icons.outlined.PostAdd
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.net.toUri
import com.skyd.raca.R
import com.skyd.raca.ui.component.RacaTopBar
import com.skyd.raca.ui.component.RacaTopBarStyle
import com.skyd.settings.BaseSettingsItem
import com.skyd.settings.SettingsLazyColumn
import com.skyd.settings.SwitchSettingsItem
import kotlinx.serialization.Serializable


@Serializable
data object EasyUseRoute

@Composable
fun EasyUseScreen() {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val context = LocalContext.current

    Scaffold(
        topBar = {
            RacaTopBar(
                style = RacaTopBarStyle.LargeFlexible,
                scrollBehavior = scrollBehavior,
                title = { Text(text = stringResource(R.string.easy_use_screen_name)) },
            )
        }
    ) { paddingValues ->
        var useAutoFill by remember {
            mutableStateOf(
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    true == getSystemService(context, AutofillManager::class.java)
                        ?.hasEnabledAutofillServices()
                } else {
                    false
                }
            )
        }

        val useAutoFillLauncher = rememberLauncherForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
            useAutoFill = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                true == getSystemService(context, AutofillManager::class.java)
                    ?.hasEnabledAutofillServices()
            } else false
        }

        SettingsLazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .nestedScroll(scrollBehavior.nestedScrollConnection),
            contentPadding = paddingValues,
        ) {
            group(text = { context.getString(R.string.easy_use_screen_send_category) }) {
                item {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        SwitchSettingsItem(
                            imageVector = Icons.Outlined.EditNote,
                            text = stringResource(id = R.string.easy_usage_screen_use_auto_fill),
                            description = stringResource(id = R.string.easy_usage_screen_use_auto_fill_description),
                            checked = useAutoFill,
                            onCheckedChange = {
                                useAutoFillLauncher.launch(
                                    Intent(ACTION_REQUEST_SET_AUTOFILL_SERVICE)
                                        .apply {
                                            data = "package:com.android.settings".toUri()
                                        }
                                )
                            },
                        )
                    } else {
                        BaseSettingsItem(
                            icon = rememberVectorPainter(image = Icons.Outlined.EditNote),
                            text = stringResource(id = R.string.easy_usage_screen_use_auto_fill),
                            descriptionText = stringResource(id = R.string.easy_usage_screen_use_auto_fill_not_support_description)
                        )
                    }
                }
            }
            group(text = { context.getString(R.string.easy_usage_screen_add_category) }) {
                item {
                    BaseSettingsItem(
                        icon = rememberVectorPainter(image = Icons.Outlined.PostAdd),
                        text = stringResource(id = R.string.easy_usage_screen_process_text_add),
                        descriptionText = stringResource(
                            id = R.string.easy_usage_screen_process_text_add_description,
                            formatArgs = arrayOf(stringResource(R.string.app_name))
                        )
                    )
                }
            }
        }
    }
}
