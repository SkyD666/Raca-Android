package com.skyd.raca.ui.screen.settings.easyusage

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings.ACTION_REQUEST_SET_AUTOFILL_SERVICE
import android.view.autofill.AutofillManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.PostAdd
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.core.content.ContextCompat.getSystemService
import com.skyd.raca.R
import com.skyd.raca.ui.component.*
import com.skyd.raca.ui.local.LocalNavController


const val EASY_USAGE_SCREEN_ROUTE = "easyUsageScreen"

@Composable
fun EasyUsageScreen() {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val navController = LocalNavController.current
    val context = LocalContext.current

    Scaffold(
        topBar = {
            RacaTopBar(
                style = RacaTopBarStyle.Large,
                scrollBehavior = scrollBehavior,
                title = { Text(text = stringResource(R.string.easy_usage_screen_name)) },
                navigationIcon = { BackIcon { navController.popBackStack() } },
            )
        }
    ) { paddingValues ->
        val useAutoFill = remember {
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
            useAutoFill.value = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                true == getSystemService(context, AutofillManager::class.java)
                    ?.hasEnabledAutofillServices()
            } else false
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .nestedScroll(scrollBehavior.nestedScrollConnection), contentPadding = paddingValues
        ) {
            item {
                CategorySettingsItem(
                    text = stringResource(id = R.string.easy_usage_screen_send_category)
                )
            }
            item {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    SwitchSettingsItem(
                        icon = Icons.Default.EditNote,
                        text = stringResource(id = R.string.easy_usage_screen_use_auto_fill),
                        description = stringResource(id = R.string.easy_usage_screen_use_auto_fill_description),
                        checked = useAutoFill,
                        onCheckedChange = {
                            useAutoFillLauncher.launch(Intent(ACTION_REQUEST_SET_AUTOFILL_SERVICE)
                                .apply {
                                    data = Uri.parse("package:com.android.settings")
                                }
                            )
                        },
                    )
                } else {
                    BaseSettingsItem(
                        icon = rememberVectorPainter(image = Icons.Default.EditNote),
                        text = stringResource(id = R.string.easy_usage_screen_use_auto_fill),
                        descriptionText = stringResource(id = R.string.easy_usage_screen_use_auto_fill_not_support_description)
                    )
                }

            }
            item {
                CategorySettingsItem(
                    text = stringResource(id = R.string.easy_usage_screen_add_category)
                )
            }
            item {
                BaseSettingsItem(
                    icon = rememberVectorPainter(image = Icons.Default.PostAdd),
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
