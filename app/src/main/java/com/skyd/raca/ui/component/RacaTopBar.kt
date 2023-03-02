package com.skyd.raca.ui.component

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.skyd.raca.R

enum class RacaTopBarStyle {
    Small, Large, CenterAligned
}

@Composable
fun RacaTopBar(
    style: RacaTopBarStyle = RacaTopBarStyle.Small,
    title: @Composable () -> Unit,
    contentPadding: @Composable () -> PaddingValues = { PaddingValues() },
    navigationIcon: @Composable () -> Unit = {},
    actions: @Composable RowScope.() -> Unit = {},
    scrollBehavior: TopAppBarScrollBehavior? = null,
) {
    val colors = when (style) {
        RacaTopBarStyle.Small -> TopAppBarDefaults.topAppBarColors()
        RacaTopBarStyle.Large -> TopAppBarDefaults.largeTopAppBarColors()
        RacaTopBarStyle.CenterAligned -> TopAppBarDefaults.centerAlignedTopAppBarColors()
    }
    val topBarModifier = Modifier.padding(contentPadding())
    when (style) {
        RacaTopBarStyle.Small -> {
            TopAppBar(
                title = title,
                modifier = topBarModifier,
                navigationIcon = navigationIcon,
                actions = actions,
                colors = colors,
                scrollBehavior = scrollBehavior
            )
        }
        RacaTopBarStyle.Large -> {
            LargeTopAppBar(
                modifier = topBarModifier,
                title = title,
                navigationIcon = navigationIcon,
                actions = actions,
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
                colors = colors,
                scrollBehavior = scrollBehavior
            )
        }
    }
}

@Composable
fun TopBarIcon(
    painter: Painter,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
    tint: Color = LocalContentColor.current,
    contentDescription: String?,
) {
    IconButton(onClick = onClick) {
        Icon(
            modifier = modifier.size(24.dp),
            painter = painter,
            tint = tint,
            contentDescription = contentDescription
        )
    }
}

@Composable
fun TopBarIcon(
    imageVector: ImageVector,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
    tint: Color = LocalContentColor.current,
    contentDescription: String?,
) {
    IconButton(onClick = onClick) {
        Icon(
            modifier = modifier.size(24.dp),
            imageVector = imageVector,
            tint = tint,
            contentDescription = contentDescription
        )
    }
}

@Composable
fun BackIcon(onClick: () -> Unit = {}) {
    TopBarIcon(
        imageVector = Icons.Rounded.ArrowBack,
        contentDescription = stringResource(id = R.string.back),
        onClick = onClick
    )
}
