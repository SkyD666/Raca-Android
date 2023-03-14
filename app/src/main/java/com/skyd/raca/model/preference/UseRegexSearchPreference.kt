package com.skyd.raca.model.preference

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import com.skyd.raca.ext.dataStore
import com.skyd.raca.ext.put
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object UseRegexSearchPreference {
    private const val USE_REGEX_SEARCH = "useRegexSearch"
    const val default = false

    val key = booleanPreferencesKey(USE_REGEX_SEARCH)

    fun put(context: Context, scope: CoroutineScope, value: Boolean) {
        scope.launch(Dispatchers.IO) {
            context.dataStore.put(key, value)
        }
    }

    fun fromPreferences(preferences: Preferences): Boolean =
        preferences[key] ?: default
}