package com.skyd.raca.ui.local

import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.compositionLocalOf
import androidx.navigation.NavController

val LocalNavController = compositionLocalOf<NavController> {
    error("LocalNavController not initialized!")
}

val LocalDarkMode = compositionLocalOf { false }

val LocalWindowSizeClass = compositionLocalOf<WindowSizeClass> {
    error("LocalWindowSizeClass not initialized!")
}