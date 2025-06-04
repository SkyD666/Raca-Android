package com.skyd.raca.ui.screen.settings.appearance

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonGroupDefaults
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.ToggleButton
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.materialkolor.ktx.from
import com.materialkolor.ktx.toneColor
import com.materialkolor.palettes.TonalPalette
import com.skyd.raca.R
import com.skyd.raca.ext.activity
import com.skyd.raca.model.preference.theme.DarkModePreference
import com.skyd.raca.model.preference.theme.ThemeNamePreference
import com.skyd.raca.ui.component.RacaTopBar
import com.skyd.raca.ui.component.RacaTopBarStyle
import com.skyd.raca.ui.component.connectedButtonShapes
import com.skyd.raca.ui.component.suspendString
import com.skyd.raca.ui.local.LocalDarkMode
import com.skyd.raca.ui.local.LocalThemeName
import com.skyd.raca.ui.theme.extractAllColors
import kotlinx.serialization.Serializable

@Serializable
data object AppearanceRoute

@Composable
fun AppearanceScreen() {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    Scaffold(
        topBar = {
            RacaTopBar(
                style = RacaTopBarStyle.LargeFlexible,
                scrollBehavior = scrollBehavior,
                title = { Text(text = stringResource(R.string.appearance_screen_name)) },
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .nestedScroll(scrollBehavior.nestedScrollConnection), contentPadding = paddingValues
        ) {
            item {
                DarkModeButtonGroup()
            }
            item {
                Palettes(colors = extractAllColors(darkTheme = false))
            }
        }
    }
}

@Composable
private fun DarkModeButtonGroup() {
    val scope = rememberCoroutineScope()
    val darkMode = LocalDarkMode.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(
            space = ButtonGroupDefaults.ConnectedSpaceBetween,
            alignment = Alignment.CenterHorizontally,
        ),
    ) {
        DarkModePreference.values.forEachIndexed { index, darkModeValue ->
            val checked = index == DarkModePreference.values.indexOf(darkMode)
            ToggleButton(
                checked = checked,
                onCheckedChange = { if (it) DarkModePreference.put(scope, darkModeValue) },
                modifier = Modifier.semantics { role = Role.RadioButton },
                shapes = ButtonGroupDefaults.connectedButtonShapes(
                    list = DarkModePreference.values,
                    index = index,
                ),
            ) {
                Text(
                    text = suspendString { DarkModePreference.toDisplayName(darkModeValue) },
                    modifier = Modifier.padding(horizontal = 16.dp),
                )
            }
        }
    }
}

@Composable
fun Palettes(
    colors: Map<String, ColorScheme>,
    themeName: String = LocalThemeName.current,
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    Row(
        modifier = Modifier
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        colors.forEach { (t, u) ->
            SelectableMiniPalette(
                selected = t == themeName,
                onClick = {
                    ThemeNamePreference.put(scope, t) {
                        context.activity.recreate()
                    }
                },
                contentDescription = { ThemeNamePreference.toDisplayName(t) },
                accents = remember(u) {
                    listOf(
                        TonalPalette.from(u.primary),
                        TonalPalette.from(u.secondary),
                        TonalPalette.from(u.tertiary)
                    )
                }
            )
        }
    }
}

@Composable
fun SelectableMiniPalette(
    selected: Boolean,
    onClick: () -> Unit,
    contentDescription: suspend () -> String,
    accents: List<TonalPalette>,
) {
    TooltipBox(
        modifier = Modifier,
        positionProvider = TooltipDefaults.rememberTooltipPositionProvider(),
        tooltip = {
            PlainTooltip {
                Text(suspendString { contentDescription() })
            }
        },
        state = rememberTooltipState()
    ) {
        Box(
            modifier = Modifier
                .size(74.dp)
                .clip(RoundedCornerShape(16.dp))
                .clickable(onClick = onClick),
        ) {
            Box(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.inverseOnSurface)
                    .fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                Surface(
                    modifier = Modifier.size(50.dp),
                    shape = CircleShape,
                    color = accents[0].toneColor(36),
                ) {
                    Box {
                        Surface(
                            modifier = Modifier
                                .size(50.dp)
                                .offset((-25).dp, 25.dp),
                            color = accents[1].toneColor(80),
                        ) {}
                        Surface(
                            modifier = Modifier
                                .size(50.dp)
                                .offset(25.dp, 25.dp),
                            color = accents[2].toneColor(65),
                        ) {}
                    }
                }
            }
            AnimatedVisibility(
                visible = selected,
                enter = fadeIn(),
                exit = fadeOut(),
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .border(
                            width = 2.dp,
                            color = accents[0].toneColor(50),
                            shape = RoundedCornerShape(16.dp),
                        )
                        .padding(2.dp),
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .border(
                                width = 2.dp,
                                color = MaterialTheme.colorScheme.surface,
                                shape = RoundedCornerShape(15.dp),
                            ),
                    )
                }
            }
        }
    }
}