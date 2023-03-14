package com.skyd.raca.ui.screen.more

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Egg
import androidx.compose.material.icons.filled.ImportExport
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.skyd.raca.R
import com.skyd.raca.ext.screenIsLand
import com.skyd.raca.model.bean.More1Bean
import com.skyd.raca.ui.component.RacaTopBar
import com.skyd.raca.ui.component.lazyverticalgrid.RacaLazyVerticalGrid
import com.skyd.raca.ui.component.lazyverticalgrid.adapter.LazyGridAdapter
import com.skyd.raca.ui.component.lazyverticalgrid.adapter.proxy.More1Proxy
import com.skyd.raca.ui.local.LocalNavController
import com.skyd.raca.ui.screen.about.ABOUT_SCREEN_ROUTE
import com.skyd.raca.ui.screen.settings.SETTINGS_SCREEN_ROUTE
import com.skyd.raca.ui.screen.settings.importexport.IMPORT_EXPORT_SCREEN_ROUTE
import com.skyd.raca.ui.component.shape.CurlyCornerShape

@Composable
fun MoreScreen() {
    val navController = LocalNavController.current
    val context = LocalContext.current

    Scaffold(
        topBar = {
            RacaTopBar(
                title = { Text(text = stringResource(id = R.string.navi_bar_more)) },
                navigationIcon = {
                    IconButton(onClick = { }) {
                        Icon(imageVector = Icons.Default.Egg, contentDescription = null)
                    }
                }
            )
        },
        contentWindowInsets = if (context.screenIsLand) {
            WindowInsets(
                left = 0,
                top = 0,
                right = ScaffoldDefaults.contentWindowInsets
                    .getRight(LocalDensity.current, LocalLayoutDirection.current),
                bottom = 0
            )
        } else {
            WindowInsets(0.dp)
        }
    ) {
        val moreList = listOf(
            More1Bean(
                title = stringResource(R.string.import_export_screen_name),
                icon = Icons.Default.ImportExport,
                iconTint = MaterialTheme.colorScheme.onPrimary,
                shape = RoundedCornerShape(30),
                shapeColor = MaterialTheme.colorScheme.primary,
                action = { navController.navigate(IMPORT_EXPORT_SCREEN_ROUTE) }
            ),
            More1Bean(
                title = stringResource(R.string.settings),
                icon = Icons.Default.Settings,
                iconTint = MaterialTheme.colorScheme.onSecondary,
                shape = CircleShape,
                shapeColor = MaterialTheme.colorScheme.secondary,
                action = { navController.navigate(SETTINGS_SCREEN_ROUTE) }
            ),
            More1Bean(
                title = stringResource(R.string.about),
                icon = Icons.Default.Info,
                iconTint = MaterialTheme.colorScheme.onTertiary,
                shape = CurlyCornerShape(amp = 5.0, count = 10),
                shapeColor = MaterialTheme.colorScheme.tertiary,
                action = { navController.navigate(ABOUT_SCREEN_ROUTE) }
            )
        )

        val adapter = remember {
            LazyGridAdapter(
                mutableListOf(
                    More1Proxy(onClickListener = { data ->
                        data.action.invoke()
                    })
                )
            )
        }
        RacaLazyVerticalGrid(
            modifier = Modifier.fillMaxSize(),
            dataList = moreList,
            adapter = adapter,
            contentPadding = it
        )
    }

}