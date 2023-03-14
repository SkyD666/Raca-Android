package com.skyd.raca.model.preference

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import com.skyd.raca.ext.dataStore
import com.skyd.raca.ext.put
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object CurrentArticleUuidPreference {
    private const val CURRENT_ARTICLE_UUID = "currentArticleUuid"
    const val default = ""

    val key = stringPreferencesKey(CURRENT_ARTICLE_UUID)

    fun put(context: Context, scope: CoroutineScope, value: String) {
        scope.launch(Dispatchers.IO) {
            context.dataStore.put(key, value)
        }
    }

    fun fromPreferences(preferences: Preferences): String = preferences[key] ?: default
}