package com.skyd.raca.ext

import androidx.compose.ui.platform.UriHandler
import co.touchlab.kermit.Logger
import com.skyd.raca.R
import com.skyd.raca.appContext

fun UriHandler.safeOpenUri(uri: String) {
    try {
        openUri(uri)
    } catch (_: IllegalArgumentException) {
        Logger.w(appContext.getString(R.string.no_browser_found, uri))
    }
}