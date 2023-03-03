package com.skyd.raca.ui.activity

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.NavHostController
import androidx.navigation.navArgument
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.skyd.raca.config.refreshDarkMode
import com.skyd.raca.ext.navigate
import com.skyd.raca.ui.local.LocalNavController
import com.skyd.raca.ui.screen.MAIN_SCREEN_ROUTE
import com.skyd.raca.ui.screen.MainScreen
import com.skyd.raca.ui.screen.about.ABOUT_SCREEN_ROUTE
import com.skyd.raca.ui.screen.about.AboutScreen
import com.skyd.raca.ui.screen.add.ADD_SCREEN_ROUTE
import com.skyd.raca.ui.screen.add.AddScreen
import com.skyd.raca.ui.screen.license.LICENSE_SCREEN_ROUTE
import com.skyd.raca.ui.screen.license.LicenseScreen
import com.skyd.raca.ui.screen.settings.SETTINGS_SCREEN_ROUTE
import com.skyd.raca.ui.screen.settings.SettingsScreen
import com.skyd.raca.ui.screen.settings.appearance.APPEARANCE_SCREEN_ROUTE
import com.skyd.raca.ui.screen.settings.appearance.AppearanceScreen
import com.skyd.raca.ui.screen.settings.easyusage.EASY_USAGE_SCREEN_ROUTE
import com.skyd.raca.ui.screen.settings.easyusage.EasyUsageScreen
import com.skyd.raca.ui.screen.settings.importexport.IMPORT_EXPORT_SCREEN_ROUTE
import com.skyd.raca.ui.screen.settings.importexport.ImportExportScreen
import com.skyd.raca.ui.screen.settings.importexport.exportdata.EXPORT_SCREEN_ROUTE
import com.skyd.raca.ui.screen.settings.importexport.exportdata.ExportScreen
import com.skyd.raca.ui.screen.settings.importexport.importdata.IMPORT_SCREEN_ROUTE
import com.skyd.raca.ui.screen.settings.importexport.importdata.ImportScreen
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
            val navController = rememberAnimatedNavController()
            CompositionLocalProvider(LocalNavController provides navController) {
                this.navController = navController
                val darkMode by refreshDarkMode.collectAsState()
                RacaTheme(darkTheme = darkMode) {
                    AnimatedNavHost(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.background),
                        navController = navController,
                        startDestination = MAIN_SCREEN_ROUTE,
                    ) {
                        composable(route = MAIN_SCREEN_ROUTE) {
                            MainScreen()
                        }
                        composable(
                            route = "$ADD_SCREEN_ROUTE?articleId={articleId}",
                            arguments = listOf(navArgument("articleId") { defaultValue = 0L })
                        ) {
                            AddScreen(
                                articleId = it.arguments?.getLong("articleId") ?: 0L,
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
                    }
                }

                initIntent()
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
