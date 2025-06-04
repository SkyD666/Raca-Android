package com.skyd.raca.ext

import androidx.lifecycle.Lifecycle
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController


fun NavBackStackEntry.lifecycleIsResumed() =
    this.lifecycle.currentState == Lifecycle.State.RESUMED

fun NavController.popBackStackWithLifecycle(): Boolean {
    if (currentBackStackEntry?.lifecycleIsResumed() == true) {
        return popBackStack()
    }
    return true
}