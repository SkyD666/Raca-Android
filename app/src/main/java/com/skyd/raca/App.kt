package com.skyd.raca

import android.app.Application
import android.content.Context
import com.skyd.raca.config.initDarkMode
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class App : Application() {

    override fun onCreate() {
        super.onCreate()
        appContext = this

        initDarkMode()
    }
}

lateinit var appContext: Context