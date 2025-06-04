package com.skyd.raca.ui.component

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.skyd.raca.R
import com.skyd.raca.ext.activity
import com.skyd.raca.ext.popBackStackWithLifecycle
import com.skyd.raca.ui.local.LocalGlobalNavController
import com.skyd.raca.ui.local.LocalNavController

enum class RacaTopBarStyle {
    Small, LargeFlexible, CenterAligned
}

@Composable
fun RacaTopBar(
    style: RacaTopBarStyle = RacaTopBarStyle.Small,
    title: @Composable () -> Unit,
    contentPadding: @Composable () -> PaddingValues = { PaddingValues() },
    navigationIcon: @Composable () -> Unit = { BackIcon() },
    windowInsets: WindowInsets = TopAppBarDefaults.windowInsets,
    colors: TopAppBarColors = TopAppBarDefaults.topAppBarColors(),
    actions: @Composable RowScope.() -> Unit = {},
    scrollBehavior: TopAppBarScrollBehavior? = null,
) {
    val topBarModifier = Modifier.padding(contentPadding())
    when (style) {
        RacaTopBarStyle.Small -> {
            TopAppBar(
                title = title,
                modifier = topBarModifier,
                navigationIcon = navigationIcon,
                actions = actions,
                windowInsets = windowInsets,
                colors = colors,
                scrollBehavior = scrollBehavior
            )
        }

        RacaTopBarStyle.LargeFlexible -> {
            LargeFlexibleTopAppBar(
                modifier = topBarModifier,
                title = title,
                navigationIcon = navigationIcon,
                actions = actions,
                windowInsets = windowInsets,
                colors = colors,
                scrollBehavior = scrollBehavior
            )
        }

        RacaTopBarStyle.CenterAligned -> {
            CenterAlignedTopAppBar(
                modifier = topBarModifier,
                title = title,
                navigationIcon = navigationIcon,
                actions = actions,
                windowInsets = windowInsets,
                colors = colors,
                scrollBehavior = scrollBehavior
            )
        }
    }
}

@Composable
fun onEmptyPopBackStack(): () -> Unit {
    val context = LocalContext.current
    return {
        context.activity.finish()
    }
}

@Composable
fun BackInvoker(): () -> Unit {
    val navController = LocalNavController.current
    val globalNavController = LocalGlobalNavController.current
    val onEmptyPopBackStack = onEmptyPopBackStack()
    return {
        if (!navController.popBackStackWithLifecycle() && globalNavController == navController) {
            onEmptyPopBackStack.invoke()
        }
    }
}

@Composable
fun BackIcon() {
    val backInvoker = BackInvoker()
    BackIcon { backInvoker.invoke() }
}

val DefaultBackClick = { }

@Composable
fun BackIcon(onClick: () -> Unit = {}) {
    RacaIconButton(
        imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
        contentDescription = stringResource(R.string.back),
        onClick = onClick
    )
}