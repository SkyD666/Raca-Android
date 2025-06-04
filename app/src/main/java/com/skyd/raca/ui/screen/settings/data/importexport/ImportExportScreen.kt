package com.skyd.raca.ui.screen.settings.data.importexport

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CloudSync
import androidx.compose.material.icons.outlined.FileDownload
import androidx.compose.material.icons.outlined.FileUpload
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
import com.skyd.raca.ui.screen.settings.data.importexport.cloud.webdav.WebDavRoute
import com.skyd.raca.ui.screen.settings.data.importexport.file.exportdata.ExportRoute
import com.skyd.raca.ui.screen.settings.data.importexport.file.importdata.ImportRoute
import kotlinx.serialization.Serializable

@Serializable
data object ImportExportRoute

@Composable
fun ImportExportScreen() {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val navController = LocalNavController.current

    Scaffold(
        topBar = {
            RacaTopBar(
                style = RacaTopBarStyle.LargeFlexible,
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
                    icon = rememberVectorPainter(image = Icons.Outlined.FileDownload),
                    text = stringResource(id = R.string.import_screen_name),
                    descriptionText = stringResource(id = R.string.import_export_screen_import_description),
                    onClick = { navController.navigate(ImportRoute) }
                )
                BaseSettingsItem(
                    icon = rememberVectorPainter(image = Icons.Outlined.FileUpload),
                    text = stringResource(id = R.string.export_screen_name),
                    descriptionText = stringResource(id = R.string.import_export_screen_export_description),
                    onClick = { navController.navigate(ExportRoute) }
                )
            }
            item {
                CategorySettingsItem(
                    text = stringResource(id = R.string.import_export_screen_using_cloud_category)
                )
            }
            item {
                BaseSettingsItem(
                    icon = rememberVectorPainter(image = Icons.Outlined.CloudSync),
                    text = stringResource(id = R.string.webdav_screen_name),
                    descriptionText = stringResource(id = R.string.import_export_screen_webdav_description),
                    onClick = { navController.navigate(WebDavRoute) }
                )
            }
        }
    }
}
