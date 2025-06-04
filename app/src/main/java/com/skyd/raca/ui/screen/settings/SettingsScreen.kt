package com.skyd.raca.ui.screen.settings

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ManageSearch
import androidx.compose.material.icons.outlined.AccessibilityNew
import androidx.compose.material.icons.outlined.Dataset
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import com.skyd.raca.R
import com.skyd.raca.ui.component.BaseSettingsItem
import com.skyd.raca.ui.component.RacaTopBar
import com.skyd.raca.ui.component.RacaTopBarStyle
import com.skyd.raca.ui.local.LocalNavController
import com.skyd.raca.ui.screen.settings.appearance.AppearanceRoute
import com.skyd.raca.ui.screen.settings.data.DataRoute
import com.skyd.raca.ui.screen.settings.easyusage.EasyUseRoute
import com.skyd.raca.ui.screen.settings.searchconfig.SearchConfigRoute
import kotlinx.serialization.Serializable

@Serializable
data object SettingsRoute

@Composable
fun SettingsScreen() {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val navController = LocalNavController.current
    Scaffold(
        topBar = {
            RacaTopBar(
                style = RacaTopBarStyle.LargeFlexible,
                scrollBehavior = scrollBehavior,
                title = { Text(text = stringResource(R.string.settings)) },
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .nestedScroll(scrollBehavior.nestedScrollConnection), contentPadding = paddingValues
        ) {
            item {
                BaseSettingsItem(
                    icon = rememberVectorPainter(Icons.Outlined.Palette),
                    text = stringResource(id = R.string.appearance_screen_name),
                    descriptionText = stringResource(id = R.string.setting_screen_appearance_description),
                    onClick = { navController.navigate(AppearanceRoute) }
                )
            }
            item {
                BaseSettingsItem(
                    icon = rememberVectorPainter(Icons.AutoMirrored.Outlined.ManageSearch),
                    text = stringResource(id = R.string.search_config_screen_name),
                    descriptionText = stringResource(id = R.string.setting_screen_search_description),
                    onClick = { navController.navigate(SearchConfigRoute) }
                )
            }
            item {
                BaseSettingsItem(
                    icon = rememberVectorPainter(Icons.Outlined.Dataset),
                    text = stringResource(id = R.string.data_screen_name),
                    descriptionText = stringResource(id = R.string.setting_screen_data_description),
                    onClick = { navController.navigate(DataRoute) }
                )
            }
            item {
                BaseSettingsItem(
                    icon = rememberVectorPainter(Icons.Outlined.AccessibilityNew),
                    text = stringResource(id = R.string.easy_use_screen_name),
                    descriptionText = stringResource(id = R.string.setting_screen_easy_usage_description),
                    onClick = { navController.navigate(EasyUseRoute) }
                )
            }
        }
    }
}
