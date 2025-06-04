package com.skyd.raca.ui.screen

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Widgets
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Widgets
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.skyd.raca.R
import com.skyd.raca.ext.isCompact
import com.skyd.raca.ext.thenIf
import com.skyd.raca.ui.local.LocalWindowSizeClass
import com.skyd.raca.ui.screen.home.HomeRoute
import com.skyd.raca.ui.screen.home.HomeScreen
import com.skyd.raca.ui.screen.more.MoreRoute
import com.skyd.raca.ui.screen.more.MoreScreen
import kotlinx.serialization.Serializable
import kotlin.reflect.KClass


@Serializable
data object MainRoute

@Composable
fun MainScreen() {
    val windowSizeClass = LocalWindowSizeClass.current
    val mainNavController = rememberNavController()

    val navigationBarOrRail: @Composable () -> Unit = @Composable {
        NavigationBarOrRail(navController = mainNavController)
    }

    Scaffold(
        bottomBar = {
            if (windowSizeClass.isCompact) {
                navigationBarOrRail()
            }
        },
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
    ) { padding ->
        Row(
            Modifier
                .fillMaxSize()
                .padding(padding)
                .consumeWindowInsets(padding)
                .thenIf(!windowSizeClass.isCompact) {
                    windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Horizontal))
                },
        ) {
            if (!windowSizeClass.isCompact) {
                navigationBarOrRail()
            }
            NavHost(
                navController = mainNavController,
                startDestination = HomeRoute,
                modifier = Modifier.weight(1f),
                enterTransition = { fadeIn(animationSpec = tween(170)) },
                exitTransition = { fadeOut(animationSpec = tween(170)) },
                popEnterTransition = { fadeIn(animationSpec = tween(170)) },
                popExitTransition = { fadeOut(animationSpec = tween(170)) },
            ) {
                composable<HomeRoute> { HomeScreen() }
                composable<MoreRoute> { MoreScreen() }
            }
        }
    }
}

private fun <T : Any> NavBackStackEntry?.selected(route: KClass<T>) =
    this?.destination?.hierarchy?.any { it.hasRoute(route) } == true

@Composable
private fun NavigationBarOrRail(navController: NavController) {
    val items = listOf(
        stringResource(R.string.navi_bar_home) to HomeRoute,
        stringResource(R.string.navi_bar_more) to MoreRoute,
    )
    val icons = remember {
        mapOf(
            true to listOf(
                Icons.Filled.Home,
                Icons.Filled.Widgets
            ),
            false to listOf(
                Icons.Outlined.Home,
                Icons.Outlined.Widgets
            ),
        )
    }

    val navBackStackEntry by navController.currentBackStackEntryAsState()

    val onClick: (Int) -> Unit = { index ->
        navController.navigate(items[index].second) {
            // Pop up to the previous (?: start) destination of the graph to
            // avoid building up a large stack of destinations on the back stack as users select items
            popUpTo(
                id = navController.currentDestination?.id
                    ?: navController.graph.findStartDestination().id
            ) {
                saveState = true
                inclusive = true
            }
            // Avoid multiple copies of the same destination when reselecting the same item
            launchSingleTop = true
            // Restore state when reselecting a previously selected item
            restoreState = true
        }
    }

    if (LocalWindowSizeClass.current.isCompact) {
        NavigationBar {
            items.forEachIndexed { index, item ->
                val selected = navBackStackEntry.selected(item.second::class)
                NavigationBarItem(
                    icon = { Icon(icons[selected]!![index], contentDescription = item.first) },
                    label = { Text(item.first) },
                    selected = selected,
                    onClick = { onClick(index) }
                )
            }
        }
    } else {
        NavigationRail {
            items.forEachIndexed { index, item ->
                val selected = navBackStackEntry.selected(item.second::class)
                NavigationRailItem(
                    icon = { Icon(icons[selected]!![index], contentDescription = item.first) },
                    label = { Text(item.first) },
                    selected = selected,
                    onClick = { onClick(index) }
                )
            }
        }
    }
}