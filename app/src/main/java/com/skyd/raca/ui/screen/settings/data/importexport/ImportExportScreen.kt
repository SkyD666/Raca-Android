package com.skyd.raca.ui.screen.settings.data.importexport

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import com.skyd.raca.R
import com.skyd.raca.ui.component.BaseSettingsItem
import com.skyd.raca.ui.component.CategorySettingsItem
import com.skyd.raca.ui.component.RacaTopBar
import com.skyd.raca.ui.component.RacaTopBarStyle
import com.skyd.raca.ui.local.LocalNavController
import com.skyd.raca.ui.screen.settings.data.importexport.cloud.webdav.WEBDAV_SCREEN_ROUTE
import com.skyd.raca.ui.screen.settings.data.importexport.file.exportdata.EXPORT_SCREEN_ROUTE
import com.skyd.raca.ui.screen.settings.data.importexport.file.importdata.IMPORT_SCREEN_ROUTE

const val IMPORT_EXPORT_SCREEN_ROUTE = "importExportScreen"

@Composable
fun ImportExportScreen() {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val navController = LocalNavController.current

    Scaffold(
        topBar = {
            RacaTopBar(
                style = RacaTopBarStyle.Large,
                scrollBehavior = scrollBehavior,
                title = { Text(text = stringResource(R.string.import_export_screen_name)) },
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .nestedScroll(scrollBehavior.nestedScrollConnection), contentPadding = paddingValues
        ) {
            item {
                CategorySettingsItem(
                    text = stringResource(id = R.string.import_export_screen_using_file_category)
                )
            }
            item {
                BaseSettingsItem(
                    icon = rememberVectorPainter(image = Icons.Default.FileDownload),
                    text = stringResource(id = R.string.import_screen_name),
                    descriptionText = stringResource(id = R.string.import_export_screen_import_description),
                    onClick = { navController.navigate(IMPORT_SCREEN_ROUTE) }
                )
                BaseSettingsItem(
                    icon = rememberVectorPainter(image = Icons.Default.FileUpload),
                    text = stringResource(id = R.string.export_screen_name),
                    descriptionText = stringResource(id = R.string.import_export_screen_export_description),
                    onClick = { navController.navigate(EXPORT_SCREEN_ROUTE) }
                )
            }
            item {
                CategorySettingsItem(
                    text = stringResource(id = R.string.import_export_screen_using_cloud_category)
                )
            }
            item {
                BaseSettingsItem(
                    icon = rememberVectorPainter(image = Icons.Default.CloudSync),
                    text = stringResource(id = R.string.webdav_screen_name),
                    descriptionText = stringResource(id = R.string.import_export_screen_webdav_description),
                    onClick = { navController.navigate(WEBDAV_SCREEN_ROUTE) }
                )
            }
        }
    }
}
