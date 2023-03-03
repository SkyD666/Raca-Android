package com.skyd.raca.ui.screen.settings.appearance

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.skyd.raca.R
import com.skyd.raca.config.darkMode
import com.skyd.raca.config.darkModeList
import com.skyd.raca.ui.component.BackIcon
import com.skyd.raca.ui.component.BaseSettingsItem
import com.skyd.raca.ui.component.RacaTopBar
import com.skyd.raca.ui.component.RacaTopBarStyle
import com.skyd.raca.ui.local.LocalNavController

const val APPEARANCE_SCREEN_ROUTE = "appearanceScreen"

@Composable
fun AppearanceScreen() {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val navController = LocalNavController.current

    var openDarkBottomSheet by rememberSaveable { mutableStateOf(false) }

    Scaffold(
        topBar = {
            RacaTopBar(
                style = RacaTopBarStyle.Large,
                scrollBehavior = scrollBehavior,
                title = { Text(text = stringResource(R.string.appearance_screen_name)) },
                navigationIcon = { BackIcon { navController.popBackStack() } },
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .nestedScroll(scrollBehavior.nestedScrollConnection), contentPadding = paddingValues
        ) {
            item {
                BaseSettingsItem(
                    icon = rememberVectorPainter(image = Icons.Default.DarkMode),
                    text = stringResource(id = R.string.appearance_screen_dark_mode),
                    descriptionText = stringResource(id = R.string.appearance_screen_dark_mode_description),
                    onClick = { openDarkBottomSheet = true }
                )
            }
        }
        if (openDarkBottomSheet) {
            DarkModeSheet {
                openDarkBottomSheet = false
            }
        }
    }
}

@Composable
private fun DarkModeSheet(onDismissRequest: () -> Unit) {
    val bottomSheetState = rememberSheetState()
    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = bottomSheetState,
    ) {
        val (selectedOption, onOptionSelected) = remember { mutableStateOf(darkMode) }

        Column(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .padding(bottom = 16.dp)
                .selectableGroup()
        ) {
            darkModeList.forEach { (v, name) ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.Transparent)
                ) {
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .selectable(
                                selected = (v == selectedOption),
                                onClick = {
                                    onOptionSelected(v)
                                    darkMode = v
                                },
                                role = Role.RadioButton
                            )
                            .padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = (v == selectedOption),
                            onClick = null // null recommended for accessibility with screen readers
                        )
                        Text(
                            text = name,
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(start = 16.dp)
                        )
                    }
                }
            }
        }
    }
}