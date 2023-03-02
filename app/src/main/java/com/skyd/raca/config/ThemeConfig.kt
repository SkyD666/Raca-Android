package com.skyd.raca.config

import android.os.Build
import androidx.appcompat.app.AppCompatDelegate
import com.skyd.raca.R
import com.skyd.raca.appContext
import com.skyd.raca.ext.editor
import com.skyd.raca.ext.sharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow

val darkModeList = mutableMapOf(
    AppCompatDelegate.MODE_NIGHT_NO to appContext.getString(R.string.dark_mode_light),
    AppCompatDelegate.MODE_NIGHT_YES to appContext.getString(R.string.dark_mode_dark)
).apply {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        put(
            AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM,
            appContext.getString(R.string.dark_mode_follow_system)
        )
    }
}

fun getDarkModeDisplayName(value: Int): String = darkModeList[value].orEmpty()

var darkMode = 0
    set(value) {
        if (value == field) return
        if (value != AppCompatDelegate.MODE_NIGHT_YES &&
            value != AppCompatDelegate.MODE_NIGHT_NO &&
            value != AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
        ) {
            throw IllegalArgumentException("darkMode value invalid!!!")
        }
        sharedPreferences().editor { putInt("darkMode", value) }
        field = value
        AppCompatDelegate.setDefaultNightMode(value)
        refreshDarkMode.tryEmit(value)
    }

fun initDarkMode() {
    darkMode = sharedPreferences().getInt(
        "darkMode", if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
        } else {
            AppCompatDelegate.MODE_NIGHT_NO
        }
    ).also { AppCompatDelegate.setDefaultNightMode(it) }
}

val refreshDarkMode: MutableStateFlow<Int> = MutableStateFlow(darkMode)
