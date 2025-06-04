package com.skyd.raca.ui.screen.settings.searchconfig

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Code
import androidx.compose.material.icons.outlined.Domain
import androidx.compose.material.icons.outlined.Done
import androidx.compose.material.icons.outlined.JoinInner
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.skyd.raca.R
import com.skyd.raca.base.LoadUiIntent
import com.skyd.raca.config.allSearchDomain
import com.skyd.raca.model.bean.SearchDomainBean
import com.skyd.raca.model.preference.IntersectSearchBySpacePreference
import com.skyd.raca.model.preference.UseRegexSearchPreference
import com.skyd.raca.ui.component.BaseSettingsItem
import com.skyd.raca.ui.component.CategorySettingsItem
import com.skyd.raca.ui.component.RacaTopBar
import com.skyd.raca.ui.component.RacaTopBarStyle
import com.skyd.raca.ui.component.SwitchSettingsItem
import com.skyd.raca.ui.component.dialog.WaitingDialog
import com.skyd.raca.ui.local.LocalIntersectSearchBySpace
import com.skyd.raca.ui.local.LocalUseRegexSearch
import kotlinx.serialization.Serializable
import org.koin.compose.viewmodel.koinViewModel

@Serializable
data object SearchConfigRoute

@Composable
fun SearchConfigScreen(viewModel: SearchConfigViewModel = koinViewModel()) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val useRegexSearch = LocalUseRegexSearch.current
    val intersectSearchBySpace = LocalIntersectSearchBySpace.current
    val searchDomainMap = remember { mutableStateMapOf<String, Boolean>() }
    var openWaitingDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.sendUiIntent(SearchConfigIntent.GetSearchDomain)
    }

    Scaffold(
        topBar = {
            RacaTopBar(
                style = RacaTopBarStyle.LargeFlexible,
                scrollBehavior = scrollBehavior,
                title = { Text(text = stringResource(R.string.search_config_screen_name)) },
            )
        }
    ) { paddingValues ->
        val selected = remember { mutableStateMapOf<Int, SnapshotStateMap<Int, Boolean>>() }
        val tables = remember { allSearchDomain.keys.toList() }

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
                    icon = Icons.Outlined.Code,
                    text = stringResource(id = R.string.search_config_screen_use_regex),
                    description = stringResource(id = R.string.search_config_screen_use_regex_description),
                    checked = useRegexSearch,
                    onCheckedChange = {
                        UseRegexSearchPreference.put(context = context, scope = scope, value = it)
                    },
                )
            }
            item {
                SwitchSettingsItem(
                    icon = Icons.Outlined.JoinInner,
                    text = stringResource(id = R.string.search_config_screen_intersect_search_by_space),
                    description = stringResource(id = R.string.search_config_screen_intersect_search_by_space_description),
                    checked = intersectSearchBySpace,
                    onCheckedChange = {
                        IntersectSearchBySpacePreference.put(
                            context = context, scope = scope, value = it
                        )
                    },
                )
            }
            item {
                CategorySettingsItem(
                    text = stringResource(id = R.string.search_config_screen_domain_category)
                )
            }
            repeat(tables.size) { tableIndex ->
                selected[tableIndex] = mutableStateMapOf()
                item {
                    SearchDomainItem(
                        selected = selected[tableIndex]!!,
                        table = tables[tableIndex],
                        searchDomain = searchDomainMap,
                        onSetSearchDomain = {
                            viewModel.sendUiIntent(SearchConfigIntent.SetSearchDomain(it))
                        }
                    )
                }
            }
        }
        viewModel.loadUiIntentFlow.collectAsStateWithLifecycle(initialValue = null).value?.also {
            when (it) {
                is LoadUiIntent.Error -> {}
                is LoadUiIntent.Loading -> {
                    openWaitingDialog = it.isShow
                }

                is LoadUiIntent.ShowMainView -> {}
            }
        }
        viewModel.uiStateFlow.collectAsStateWithLifecycle().value.apply {
            when (searchDomainResultUiState) {
                is SearchDomainResultUiState.Success -> {
                    searchDomainMap.clear()
                    searchDomainMap.putAll(searchDomainResultUiState.searchDomainMap)
                }

                SearchDomainResultUiState.Init -> {}
            }
        }
        WaitingDialog(visible = openWaitingDialog)
    }
}

@Composable
fun SearchDomainItem(
    selected: SnapshotStateMap<Int, Boolean>,
    table: Pair<String, String>,
    searchDomain: Map<String, Boolean>,
    onSetSearchDomain: (SearchDomainBean) -> Unit,
    icon: Painter = rememberVectorPainter(image = Icons.Outlined.Domain),
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
                horizontalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                repeat(columns.size) { columnIndex ->
                    val (columnName, columnDisplayName) = columns[columnIndex]
                    selected[columnIndex] = searchDomain["${tableName}/${columnName}"] ?: false

                    FilterChip(
                        selected = selected[columnIndex] ?: false,
                        onClick = {
                            selected[columnIndex] = !(selected[columnIndex] ?: false)
                            onSetSearchDomain(
                                SearchDomainBean(
                                    tableName = tableName,
                                    columnName = columnName,
                                    search = selected[columnIndex] ?: false
                                )
                            )
                        },
                        label = { Text(columnDisplayName) },
                        leadingIcon = if (selected[columnIndex] == true) {
                            {
                                Icon(
                                    imageVector = Icons.Outlined.Done,
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
