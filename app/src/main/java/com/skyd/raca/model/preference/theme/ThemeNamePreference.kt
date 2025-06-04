package com.skyd.raca.model.preference.theme

import android.content.Context
import androidx.compose.ui.graphics.Color
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import com.skyd.raca.R
import com.skyd.raca.di.get
import com.skyd.raca.ext.dataStore
import com.skyd.raca.ext.put
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

object ThemeNamePreference {
    private const val THEME_NAME = "themeName"
    const val CUSTOM = "Custom"
    const val DYNAMIC = "Dynamic"
    const val BLUE = "Blue"
    const val PINK = "Pink"
    const val YELLOW = "Yellow"
    const val RED = "Red"
    const val GREEN = "Green"
    const val PURPLE = "Purple"
    const val MAHIRO = "Mahiro"

    val basicValues = arrayOf(BLUE, PINK, YELLOW, RED, GREEN, PURPLE, MAHIRO)

    val values: List<ThemeItem> = mutableListOf(
        ThemeItem(
            name = PURPLE,
            keyColor = Color(0xFF62539f)
        ),
        ThemeItem(
            name = BLUE,
            keyColor = Color(0xFF80BBFF)
        ),
        ThemeItem(
            name = PINK,
            keyColor = Color(0xFFFFD8E4)
        ),
        ThemeItem(
            name = YELLOW,
            keyColor = Color(0xFFE9B666)
        ),
        ThemeItem(
            name = GREEN,
            keyColor = Color(0xFF4CAF50)
        ),
    )

    val default = values[0].name

    val key = stringPreferencesKey(THEME_NAME)

    suspend fun toDisplayName(value: String): String = get<Context>().getString(
        when (value) {
            DYNAMIC -> R.string.theme_dynamic
            BLUE -> R.string.theme_blue
            PINK -> R.string.theme_pink
            YELLOW -> R.string.theme_yellow
            RED -> R.string.theme_red
            GREEN -> R.string.theme_green
            PURPLE -> R.string.theme_purple
            MAHIRO -> R.string.theme_mahiro
            else -> R.string.unknown
        }
    )

    fun toColors(value: String): Triple<Color, Color?, Color?> = Triple(
        toSeedColor(value),
        toSecondaryColor(value),
        toTertiaryColor(value),
    )

    fun toSeedColor(value: String): Color = when (value) {
        BLUE -> Color(0xFF006EBE)
        PINK -> Color(0xFFFF7AA3)
        YELLOW -> Color(0xFFFABE03)
        RED -> Color(0xFFB90037)
        GREEN -> Color(0xFF3F975B)
        PURPLE -> Color(0xFF7E6195)
        MAHIRO -> Color(0xFFEAD4CE)
        else -> Color(0xFF006EBE)
    }

    fun toSecondaryColor(value: String): Color? = when (value) {
        MAHIRO -> Color(0xFF7D859D)
        else -> null
    }

    fun toTertiaryColor(value: String): Color? = when (value) {
        MAHIRO -> Color(0xFFEC9CA8)
        else -> null
    }

    fun put(scope: CoroutineScope, value: String, onSuccess: () -> Unit) {
        scope.launch(Dispatchers.IO) {
            get<Context>().dataStore.put(key, value)
            withContext(Dispatchers.Main) {
                onSuccess()
            }
        }
    }

    fun fromPreferences(preferences: Preferences) = preferences[key] ?: default

    data class ThemeItem(val name: String, val keyColor: Color)
}
