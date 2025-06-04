package com.skyd.raca.ui.theme

import android.app.UiModeManager
import android.content.Context
import android.os.Build
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialExpressiveTheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.materialkolor.Contrast
import com.materialkolor.dynamicColorScheme
import com.materialkolor.rememberDynamicColorScheme
import com.skyd.raca.di.get
import com.skyd.raca.model.preference.theme.DarkModePreference
import com.skyd.raca.model.preference.theme.ThemeNamePreference
import com.skyd.raca.ui.local.LocalThemeName

@Composable
fun RacaTheme(
    darkTheme: Int,
    content: @Composable () -> Unit
) {
    RacaTheme(
        darkTheme = DarkModePreference.inDark(darkTheme),
        content = content
    )
}

@Composable
fun RacaTheme(
    darkTheme: Boolean,
    colors: Map<String, ColorScheme> = extractAllColors(darkTheme),
    content: @Composable () -> Unit
) {
    val themeName = LocalThemeName.current
    val isAmoled = false//AmoledDarkModePreference.current

    MaterialExpressiveTheme(
        colorScheme = remember(themeName, darkTheme, isAmoled) {
            colors.getOrElse(themeName) {
                val (primary, secondary, tertiary) = ThemeNamePreference.toColors(
                    ThemeNamePreference.basicValues[0]
                )
                dynamicColorScheme(
                    seedColor = primary,
                    isDark = darkTheme,
                    isAmoled = isAmoled,
                    secondary = secondary,
                    tertiary = tertiary,
                    contrastLevel = contrastLevel(),
                )
            }
        },
        typography = Typography,
        content = content
    )
}

fun contrastLevel(): Double {
    var contrastLevel: Double = Contrast.Default.value
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
        val uiModeManager =
            get<Context>().getSystemService(Context.UI_MODE_SERVICE) as UiModeManager
        contrastLevel = uiModeManager.contrast.toDouble()
    }
    return contrastLevel
}

@Composable
fun extractAllColors(darkTheme: Boolean): Map<String, ColorScheme> {
    return extractDynamicColor(darkTheme) + extractColors(darkTheme)
}

@Composable
fun extractColors(darkTheme: Boolean): Map<String, ColorScheme> {
    return ThemeNamePreference.basicValues.associateWith {
        val (primary, secondary, tertiary) = ThemeNamePreference.toColors(it)
        rememberDynamicColorScheme(
            primary = primary,
            isDark = darkTheme,
            isAmoled = false,//AmoledDarkModePreference.current,
            secondary = secondary,
            tertiary = tertiary,
            contrastLevel = contrastLevel(),
        )
    }.toMutableMap()
}

@Composable
fun extractDynamicColor(darkTheme: Boolean): Map<String, ColorScheme> = buildMap {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val context = LocalContext.current
        put(
            ThemeNamePreference.DYNAMIC,
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        )
    }
}