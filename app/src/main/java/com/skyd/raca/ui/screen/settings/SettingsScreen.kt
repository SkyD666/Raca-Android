package com.skyd.raca.ui.screen.settings

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ManageSearch
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import com.skyd.raca.R
import com.skyd.raca.ui.component.BackIcon
import com.skyd.raca.ui.component.BaseSettingsItem
import com.skyd.raca.ui.component.RacaTopBar
import com.skyd.raca.ui.component.RacaTopBarStyle
import com.skyd.raca.ui.local.LocalNavController
import com.skyd.raca.ui.screen.settings.appearance.APPEARANCE_SCREEN_ROUTE
import com.skyd.raca.ui.screen.settings.searchconfig.SEARCH_CONFIG_SCREEN_ROUTE

const val SETTINGS_SCREEN_ROUTE = "settingsScreen"

@Composable
fun SettingsScreen() {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val navController = LocalNavController.current
    Scaffold(
        topBar = {
            RacaTopBar(
                style = RacaTopBarStyle.Large,
                scrollBehavior = scrollBehavior,
                title = { Text(text = stringResource(R.string.settings)) },
                navigationIcon = { BackIcon { navController.popBackStack() } },
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
                    icon = rememberVectorPainter(Icons.Default.ManageSearch),
                    text = stringResource(id = R.string.search_config_screen_name),
                    descriptionText = stringResource(id = R.string.setting_screen_search_description),
                    onClick = { navController.navigate(SEARCH_CONFIG_SCREEN_ROUTE) }
                )
            }
            item {
                BaseSettingsItem(
                    icon = rememberVectorPainter(Icons.Default.Palette),
                    text = stringResource(id = R.string.appearance_screen_name),
                    descriptionText = stringResource(id = R.string.setting_screen_appearance_description),
                    onClick = { navController.navigate(APPEARANCE_SCREEN_ROUTE) }
                )
            }
        }
    }
}
