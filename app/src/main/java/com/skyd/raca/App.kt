package com.skyd.raca

import android.app.Application
import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import com.skyd.raca.ext.dataStore
import com.skyd.raca.ext.get
import com.skyd.raca.model.preference.theme.DarkModePreference
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class App : Application() {

    override fun onCreate() {
        super.onCreate()
        appContext = this

        AppCompatDelegate.setDefaultNightMode(
            dataStore.get(DarkModePreference.key) ?: DarkModePreference.default
        )

    }
}

lateinit var appContext: Context