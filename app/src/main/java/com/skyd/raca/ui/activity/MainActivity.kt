package com.skyd.raca.ui.activity

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.NavHostController
import androidx.navigation.NavUri
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.skyd.raca.model.preference.SettingsProvider
import com.skyd.raca.ui.component.ExternalUriHandler
import com.skyd.raca.ui.component.RacaNavHost
import com.skyd.raca.ui.local.LocalDarkMode
import com.skyd.raca.ui.local.LocalGlobalNavController
import com.skyd.raca.ui.local.LocalNavController
import com.skyd.raca.ui.local.LocalWindowSizeClass
import com.skyd.raca.ui.screen.MainRoute
import com.skyd.raca.ui.screen.MainScreen
import com.skyd.raca.ui.screen.about.AboutRoute
import com.skyd.raca.ui.screen.about.AboutScreen
import com.skyd.raca.ui.screen.about.license.LicenseRoute
import com.skyd.raca.ui.screen.about.license.LicenseScreen
import com.skyd.raca.ui.screen.add.AddRoute
import com.skyd.raca.ui.screen.add.AddRoute.Companion.AddLauncher
import com.skyd.raca.ui.screen.minitool.MiniToolRoute
import com.skyd.raca.ui.screen.minitool.MiniToolScreen
import com.skyd.raca.ui.screen.minitool.abstractemoji.AbstractEmojiRoute
import com.skyd.raca.ui.screen.minitool.abstractemoji.AbstractEmojiScreen
import com.skyd.raca.ui.screen.settings.SettingsRoute
import com.skyd.raca.ui.screen.settings.SettingsScreen
import com.skyd.raca.ui.screen.settings.appearance.AppearanceRoute
import com.skyd.raca.ui.screen.settings.appearance.AppearanceScreen
import com.skyd.raca.ui.screen.settings.data.DataRoute
import com.skyd.raca.ui.screen.settings.data.DataScreen
import com.skyd.raca.ui.screen.settings.data.importexport.ImportExportRoute
import com.skyd.raca.ui.screen.settings.data.importexport.ImportExportScreen
import com.skyd.raca.ui.screen.settings.data.importexport.cloud.webdav.WebDavRoute
import com.skyd.raca.ui.screen.settings.data.importexport.cloud.webdav.WebDavScreen
import com.skyd.raca.ui.screen.settings.data.importexport.file.exportdata.ExportRoute
import com.skyd.raca.ui.screen.settings.data.importexport.file.exportdata.ExportScreen
import com.skyd.raca.ui.screen.settings.data.importexport.file.importdata.ImportRoute
import com.skyd.raca.ui.screen.settings.data.importexport.file.importdata.ImportScreen
import com.skyd.raca.ui.screen.settings.easyusage.EasyUseRoute
import com.skyd.raca.ui.screen.settings.easyusage.EasyUseScreen
import com.skyd.raca.ui.screen.settings.searchconfig.SearchConfigRoute
import com.skyd.raca.ui.screen.settings.searchconfig.SearchConfigScreen
import com.skyd.raca.ui.theme.RacaTheme


class MainActivity : AppCompatActivity() {
    private lateinit var navController: NavHostController

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        // A known bug: https://issuetracker.google.com/issues/387281251
        enableEdgeToEdge()

        setContent {
            navController = rememberNavController()
            SettingsProvider {
                CompositionLocalProvider(
                    LocalWindowSizeClass provides calculateWindowSizeClass(this),
                    LocalGlobalNavController provides navController,
                    LocalNavController provides navController,
                ) {
                    MainNavHost()
                    IntentHandler()
                }
            }
        }
    }

    private fun initIntent() {
        val text: String = if (Intent.ACTION_SEND == intent.action && intent.type == "text/plain") {
            intent.getStringExtra(Intent.EXTRA_TEXT)
        } else {
            intent.getStringExtra(Intent.EXTRA_PROCESS_TEXT)
        } ?: return

//        TODO
//        val readonly = intent.getBooleanExtra(Intent.EXTRA_PROCESS_TEXT_READONLY, true)
//        navController.currentBackStackEntry?.savedStateHandle?.set("article", text)
//        navController.navigate(ADD_SCREEN_ROUTE, Bundle().apply { putString("article", text) })
//        if(!readonly){
//            val intent = Intent()
//            intent.putExtra(Intent.EXTRA_PROCESS_TEXT, "outputText")
//            setResult(RESULT_OK, intent)
//        }
    }
}

@Composable
private fun IntentHandler() {
    val navController = LocalNavController.current

    DisposableEffect(navController) {
        ExternalUriHandler.listener = { uri ->
            navController.navigate(NavUri(uri))
        }
        onDispose {
            // Removes the listener when the composable is no longer active
            ExternalUriHandler.listener = null
        }
    }
}

@Composable
private fun MainNavHost() {
    RacaTheme(darkTheme = LocalDarkMode.current) {
        RacaNavHost(
            navController = LocalNavController.current,
            startDestination = MainRoute,
        ) {
            composable<MainRoute> { MainScreen() }
            composable<AddRoute>(deepLinks = AddRoute.deepLinks) { AddLauncher(it) }
            composable<SettingsRoute> { SettingsScreen() }
            composable<SearchConfigRoute> { SearchConfigScreen() }
            composable<AppearanceRoute> { AppearanceScreen() }
            composable<AboutRoute> { AboutScreen() }
            composable<LicenseRoute> { LicenseScreen() }
            composable<ImportExportRoute> { ImportExportScreen() }
            composable<ImportRoute> { ImportScreen() }
            composable<ExportRoute> { ExportScreen() }
            composable<EasyUseRoute> { EasyUseScreen() }
            composable<WebDavRoute> { WebDavScreen() }
            composable<DataRoute> { DataScreen() }
            composable<MiniToolRoute> { MiniToolScreen() }
            composable<AbstractEmojiRoute> { AbstractEmojiScreen() }
        }
    }
}
