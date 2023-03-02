package com.skyd.raca.ui.screen.settings.searchconfig

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Domain
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.google.accompanist.flowlayout.FlowRow
import com.skyd.raca.R
import com.skyd.raca.config.allSearchDomain
import com.skyd.raca.config.getSearchDomain
import com.skyd.raca.config.setSearchDomain
import com.skyd.raca.config.useRegexSearch
import com.skyd.raca.ui.component.*
import com.skyd.raca.ui.local.LocalNavController

const val SEARCH_CONFIG_SCREEN_ROUTE = "searchConfigScreen"

@Composable
fun SearchConfigScreen() {
    val scrollBehavior =
        TopAppBarDefaults.exitUntilCollapsedScrollBehavior(state = rememberTopAppBarState())
    val navController = LocalNavController.current
    Scaffold(
        topBar = {
            RacaTopBar(
                style = RacaTopBarStyle.Large,
                scrollBehavior = scrollBehavior,
                title = { Text(text = stringResource(R.string.search_config_screen_name)) },
                navigationIcon = { BackIcon { navController.popBackStack() } },
            )
        }
    ) { paddingValues ->
        val selected = remember { mutableStateMapOf<Int, SnapshotStateMap<Int, Boolean>>() }
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .nestedScroll(scrollBehavior.nestedScrollConnection), contentPadding = paddingValues
        ) {
            item {
                CategorySettingsItem(
                    text = stringResource(id = R.string.search_config_screen_common_category)
                )
            }
            item {
                SwitchSettingsItem(
                    icon = Icons.Default.Code,
                    text = stringResource(id = R.string.search_config_screen_use_regex),
                    description = stringResource(id = R.string.search_config_screen_use_regex_description),
                    checked = remember { mutableStateOf(useRegexSearch) },
                    onCheckedChange = { useRegexSearch = it },
                )
            }
            item {
                CategorySettingsItem(
                    text = stringResource(id = R.string.search_config_screen_domain_category)
                )
            }
            val tables = allSearchDomain.keys.toList()
            repeat(tables.size) { tableIndex ->
                selected[tableIndex] = mutableStateMapOf()
                item {
                    SearchDomainItem(
                        selected = selected[tableIndex]!!,
                        table = tables[tableIndex]
                    )
                }
            }
        }
    }
}

@Composable
fun SearchDomainItem(
    selected: SnapshotStateMap<Int, Boolean>,
    table: Pair<String, String>,
    icon: Painter = rememberVectorPainter(image = Icons.Default.Domain),
) {
    val (tableName, tableDisplayName) = table
    BaseSettingsItem(
        icon = icon,
        text = tableDisplayName,
        onClick = {},
        description = {
            val columns = allSearchDomain[table].orEmpty()
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                mainAxisSpacing = 6.dp,
            ) {
                repeat(columns.size) { columnIndex ->
                    val (columnName, columnDisplayName) = columns[columnIndex]
                    selected[columnIndex] = getSearchDomain(tableName, columnName)

                    FilterChip(
                        selected = selected[columnIndex] ?: false,
                        onClick = {
                            selected[columnIndex] =
                                !(selected[columnIndex] ?: false)
                            setSearchDomain(
                                tableName = tableName,
                                columnName = columnName,
                                search = selected[columnIndex] ?: false
                            )
                        },
                        label = { Text(columnDisplayName) },
                        leadingIcon = if (selected[columnIndex] == true) {
                            {
                                Icon(
                                    imageVector = Icons.Default.Done,
                                    contentDescription = null,
                                    modifier = Modifier.size(FilterChipDefaults.IconSize)
                                )
                            }
                        } else {
                            null
                        }
                    )
                }
            }
        }
    )
}
