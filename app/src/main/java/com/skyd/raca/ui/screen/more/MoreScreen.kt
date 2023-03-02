package com.skyd.raca.ui.screen.more

import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.ui.res.stringResource
import com.skyd.raca.R
import com.skyd.raca.model.bean.More1Bean
import com.skyd.raca.ui.component.RacaTopBar
import com.skyd.raca.ui.component.lazyverticalgrid.RacaLazyVerticalGrid
import com.skyd.raca.ui.component.lazyverticalgrid.adapter.LazyGridAdapter
import com.skyd.raca.ui.component.lazyverticalgrid.adapter.proxy.More1Proxy
import com.skyd.raca.ui.local.LocalNavController
import com.skyd.raca.ui.screen.about.ABOUT_SCREEN_ROUTE
import com.skyd.raca.ui.screen.settings.SETTINGS_SCREEN_ROUTE
import com.skyd.raca.ui.screen.settings.importexport.IMPORT_EXPORT_SCREEN_ROUTE
import com.skyd.raca.ui.shape.CurlyCornerShape

@Composable
fun MoreScreen() {
    val navController = LocalNavController.current

    Scaffold(topBar = {
        RacaTopBar(
            title = { Text(text = stringResource(id = R.string.navi_bar_more)) },
            navigationIcon = {
                IconButton(onClick = { }) {
                    Icon(imageVector = Icons.Default.Egg, contentDescription = null)
                }
            }
        )
    }) {
        val moreList = listOf(
            More1Bean(
                title = stringResource(R.string.import_export_screen_name),
                icon = Icons.Default.ImportExport,
                shape = RoundedCornerShape(30),
                shapeColor = MaterialTheme.colorScheme.primaryContainer,
                action = { navController.navigate(IMPORT_EXPORT_SCREEN_ROUTE) }
            ),
            More1Bean(
                title = stringResource(R.string.settings),
                icon = Icons.Default.Settings,
                shape = RoundedCornerShape(30),
                shapeColor = MaterialTheme.colorScheme.secondaryContainer,
                action = { navController.navigate(SETTINGS_SCREEN_ROUTE) }
            ),
            More1Bean(
                title = stringResource(R.string.about),
                icon = Icons.Default.Info,
                shape = CurlyCornerShape(amp = 6.0),
                shapeColor = MaterialTheme.colorScheme.tertiaryContainer,
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