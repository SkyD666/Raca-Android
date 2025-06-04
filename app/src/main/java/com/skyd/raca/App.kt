package com.skyd.raca

import android.app.Application
import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import com.skyd.raca.di.databaseModule
import com.skyd.raca.di.repositoryModule
import com.skyd.raca.di.viewModelModule
import com.skyd.raca.ext.dataStore
import com.skyd.raca.ext.get
import com.skyd.raca.model.preference.theme.DarkModePreference
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.GlobalContext.startKoin

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        appContext = this

        startKoin {
            androidLogger()
            androidContext(this@App)
            modules(databaseModule, repositoryModule, viewModelModule)
        }
        AppCompatDelegate.setDefaultNightMode(
            dataStore.get(DarkModePreference.key) ?: DarkModePreference.default
        )
    }
}

lateinit var appContext: Context