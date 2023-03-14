package com.skyd.raca.model.preference

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.skyd.raca.ext.dataStore
import com.skyd.raca.ext.toSettings
import com.skyd.raca.model.preference.theme.CustomPrimaryColorPreference
import com.skyd.raca.model.preference.theme.DarkModePreference
import com.skyd.raca.model.preference.theme.ThemeNamePreference
import com.skyd.raca.ui.local.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map

data class Settings(
    // Theme
    val themeName: String = ThemeNamePreference.default,
    val customPrimaryColor: String = CustomPrimaryColorPreference.default,
    val darkMode: Int = DarkModePreference.default,

    // Article
    val currentArticleUuid: String = CurrentArticleUuidPreference.default,
    // Search
    val useRegexSearch: Boolean = UseRegexSearchPreference.default,
    // WebDav
    val webDavServer: String = WebDavServerPreference.default,
)

@Composable
fun SettingsProvider(
    content: @Composable () -> Unit,
) {
    val context = LocalContext.current
    val settings = remember {
        context.dataStore.data.map { it.toSettings() }
    }.collectAsState(initial = Settings(), context = Dispatchers.Default).value

    CompositionLocalProvider(
        // Theme
        LocalThemeName provides settings.themeName,
        LocalCustomPrimaryColor provides settings.customPrimaryColor,
        LocalDarkMode provides settings.darkMode,
        // Article
        LocalCurrentArticleUuid provides settings.currentArticleUuid,
        // Search
        LocalUseRegexSearch provides settings.useRegexSearch,
        // WebDav
        LocalWebDavServer provides settings.webDavServer,
    ) {
        content()
    }
}