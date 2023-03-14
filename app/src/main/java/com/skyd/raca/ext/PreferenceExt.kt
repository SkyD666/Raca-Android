package com.skyd.raca.ext

import androidx.datastore.preferences.core.Preferences
import com.skyd.raca.model.preference.*
import com.skyd.raca.model.preference.theme.CustomPrimaryColorPreference
import com.skyd.raca.model.preference.theme.DarkModePreference
import com.skyd.raca.model.preference.theme.ThemeNamePreference

fun Preferences.toSettings(): Settings {
    return Settings(
        // Theme
        themeName = ThemeNamePreference.fromPreferences(this),
        customPrimaryColor = CustomPrimaryColorPreference.fromPreferences(this),
        darkMode = DarkModePreference.fromPreferences(this),

        // Article
        currentArticleUuid = CurrentArticleUuidPreference.fromPreferences(this),
        query = QueryPreference.fromPreferences(this),

        // Search
        useRegexSearch = UseRegexSearchPreference.fromPreferences(this),

        // WebDav
        webDavServer = WebDavServerPreference.fromPreferences(this),
    )
}
