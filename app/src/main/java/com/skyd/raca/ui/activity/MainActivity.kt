package com.skyd.raca.ui.activity

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.background
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.NavHostController
import androidx.navigation.navArgument
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.skyd.raca.ext.navigate
import com.skyd.raca.model.preference.SettingsProvider
import com.skyd.raca.ui.local.LocalDarkMode
import com.skyd.raca.ui.local.LocalNavController
import com.skyd.raca.ui.screen.MAIN_SCREEN_ROUTE
import com.skyd.raca.ui.screen.MainScreen
import com.skyd.raca.ui.screen.about.ABOUT_SCREEN_ROUTE
import com.skyd.raca.ui.screen.about.AboutScreen
import com.skyd.raca.ui.screen.about.license.LICENSE_SCREEN_ROUTE
import com.skyd.raca.ui.screen.about.license.LicenseScreen
import com.skyd.raca.ui.screen.add.ADD_SCREEN_ROUTE
import com.skyd.raca.ui.screen.add.AddScreen
import com.skyd.raca.ui.screen.settings.SETTINGS_SCREEN_ROUTE
import com.skyd.raca.ui.screen.settings.SettingsScreen
import com.skyd.raca.ui.screen.settings.appearance.APPEARANCE_SCREEN_ROUTE
import com.skyd.raca.ui.screen.settings.appearance.AppearanceScreen
import com.skyd.raca.ui.screen.settings.easyusage.EASY_USAGE_SCREEN_ROUTE
import com.skyd.raca.ui.screen.settings.easyusage.EasyUsageScreen
import com.skyd.raca.ui.screen.settings.importexport.IMPORT_EXPORT_SCREEN_ROUTE
import com.skyd.raca.ui.screen.settings.importexport.ImportExportScreen
import com.skyd.raca.ui.screen.settings.importexport.cloud.webdav.WEBDAV_SCREEN_ROUTE
import com.skyd.raca.ui.screen.settings.importexport.cloud.webdav.WebDavScreen
import com.skyd.raca.ui.screen.settings.importexport.file.exportdata.EXPORT_SCREEN_ROUTE
import com.skyd.raca.ui.screen.settings.importexport.file.exportdata.ExportScreen
import com.skyd.raca.ui.screen.settings.importexport.file.importdata.IMPORT_SCREEN_ROUTE
import com.skyd.raca.ui.screen.settings.importexport.file.importdata.ImportScreen
import com.skyd.raca.ui.screen.settings.searchconfig.SEARCH_CONFIG_SCREEN_ROUTE
import com.skyd.raca.ui.screen.settings.searchconfig.SearchConfigScreen
import com.skyd.raca.ui.theme.RacaTheme
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var navController: NavHostController

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()

        super.onCreate(savedInstanceState)

        setContent {
            navController = rememberAnimatedNavController()
            SettingsProvider {
                CompositionLocalProvider(LocalNavController provides navController) {
                    AppContent()
                    initIntent()
                }
            }
        }
    }

    @Composable
    private fun AppContent() {
        RacaTheme(darkTheme = LocalDarkMode.current) {
            AnimatedNavHost(
                modifier = Modifier.background(MaterialTheme.colorScheme.background),
                navController = navController,
                startDestination = MAIN_SCREEN_ROUTE,
                enterTransition = {
                    fadeIn(animationSpec = tween(220, delayMillis = 90)) +
                            scaleIn(
                                initialScale = 0.92f,
                                animationSpec = tween(220, delayMillis = 90)
                            )
                },
                exitTransition = {
                    fadeOut(animationSpec = tween(90))
                },
                popEnterTransition = {
                    fadeIn(animationSpec = tween(220, delayMillis = 90)) +
                            scaleIn(
                                initialScale = 0.92f,
                                animationSpec = tween(220, delayMillis = 90)
                            )
                },
                popExitTransition = {
                    fadeOut(animationSpec = tween(90))
                },
            ) {
                composable(route = MAIN_SCREEN_ROUTE) {
                    MainScreen()
                }
                composable(
                    route = "$ADD_SCREEN_ROUTE?articleUuid={articleUuid}",
                    arguments = listOf(navArgument("articleUuid") { defaultValue = "" })
                ) {
                    AddScreen(
                        articleUuid = it.arguments?.getString("articleUuid").orEmpty(),
                        article = it.arguments?.getString("article").orEmpty()
                    )
                }
                composable(route = SETTINGS_SCREEN_ROUTE) {
                    SettingsScreen()
                }
                composable(route = SEARCH_CONFIG_SCREEN_ROUTE) {
                    SearchConfigScreen()
                }
                composable(route = APPEARANCE_SCREEN_ROUTE) {
                    AppearanceScreen()
                }
                composable(route = ABOUT_SCREEN_ROUTE) {
                    AboutScreen()
                }
                composable(route = LICENSE_SCREEN_ROUTE) {
                    LicenseScreen()
                }
                composable(route = IMPORT_EXPORT_SCREEN_ROUTE) {
                    ImportExportScreen()
                }
                composable(route = IMPORT_SCREEN_ROUTE) {
                    ImportScreen()
                }
                composable(route = EXPORT_SCREEN_ROUTE) {
                    ExportScreen()
                }
                composable(route = EASY_USAGE_SCREEN_ROUTE) {
                    EasyUsageScreen()
                }
                composable(route = WEBDAV_SCREEN_ROUTE) {
                    WebDavScreen()
                }
            }
        }
    }

    private fun initIntent() {
        val text = intent.getStringExtra(Intent.EXTRA_PROCESS_TEXT) ?: return
//        TODO
//        val readonly = intent.getBooleanExtra(Intent.EXTRA_PROCESS_TEXT_READONLY, true)
//        navController.currentBackStackEntry?.savedStateHandle?.set("article", text)
        navController.navigate(ADD_SCREEN_ROUTE, Bundle().apply { putString("article", text) })
//        if(!readonly){
//            val intent = Intent()
//            intent.putExtra(Intent.EXTRA_PROCESS_TEXT, "outputText")
//            setResult(RESULT_OK, intent)
//        }
    }
}
