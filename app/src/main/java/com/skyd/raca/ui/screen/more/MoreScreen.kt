package com.skyd.raca.ui.screen.more

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Extension
import androidx.compose.material.icons.outlined.ImportExport
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.toShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.skyd.raca.R
import com.skyd.raca.ext.isCompact
import com.skyd.raca.ext.plus
import com.skyd.raca.model.bean.MoreBean
import com.skyd.raca.ui.component.RacaTopBar
import com.skyd.raca.ui.local.LocalNavController
import com.skyd.raca.ui.local.LocalWindowSizeClass
import com.skyd.raca.ui.screen.about.AboutRoute
import com.skyd.raca.ui.screen.minitool.MiniToolRoute
import com.skyd.raca.ui.screen.settings.SettingsRoute
import com.skyd.raca.ui.screen.settings.data.importexport.ImportExportRoute
import kotlinx.serialization.Serializable

@Serializable
data object MoreRoute

@Composable
fun MoreScreen() {
    val navController = LocalNavController.current
    val context = LocalContext.current
    val windowSizeClass = LocalWindowSizeClass.current

    Scaffold(
        topBar = {
            RacaTopBar(
                title = { Text(text = stringResource(id = R.string.navi_bar_more)) },
                navigationIcon = { },
                windowInsets = WindowInsets.safeDrawing.only(
                    (WindowInsetsSides.Top + WindowInsetsSides.Right).run {
                        if (windowSizeClass.isCompact) plus(WindowInsetsSides.Left) else this
                    }
                )
            )
        },
        contentWindowInsets = WindowInsets.safeDrawing.only(
            (WindowInsetsSides.Top + WindowInsetsSides.Right).run {
                if (windowSizeClass.isCompact) plus(WindowInsetsSides.Left) else this
            }
        )
    ) {
        val colorScheme = MaterialTheme.colorScheme
        val dataList = remember {
            getMoreBeanList(
                context = context,
                colorScheme = colorScheme,
                navController = navController,
            )
        }
        LazyVerticalGrid(
            modifier = Modifier.fillMaxSize(),
            contentPadding = it + PaddingValues(horizontal = 16.dp, vertical = 10.dp),
            columns = GridCells.Adaptive(135.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            items(dataList) { item ->
                MoreItem(
                    data = item,
                    onClickListener = { data -> data.action.invoke() }
                )
            }
        }
    }
}

@Composable
fun MoreItem(
    data: MoreBean,
    onClickListener: ((data: MoreBean) -> Unit)? = null
) {
    OutlinedCard(shape = RoundedCornerShape(16)) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .combinedClickable(
                    onClick = {
                        onClickListener?.invoke(data)
                    }
                )
                .padding(25.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .padding(5.dp)
                    .background(
                        color = data.shapeColor,
                        shape = data.shape.toShape(),
                    )
                    .padding(16.dp)
            ) {
                Icon(
                    modifier = Modifier.size(35.dp),
                    imageVector = data.icon,
                    contentDescription = null,
                    tint = data.iconTint
                )
            }
            Text(
                modifier = Modifier
                    .padding(top = 15.dp)
                    .basicMarquee(iterations = Int.MAX_VALUE),
                text = data.title,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 1,
                textAlign = TextAlign.Center
            )
        }
    }
}

private fun getMoreBeanList(
    context: Context,
    colorScheme: ColorScheme,
    navController: NavController,
): MutableList<MoreBean> = mutableListOf(
    MoreBean(
        title = context.getString(R.string.import_export_screen_name),
        icon = Icons.Outlined.ImportExport,
        iconTint = colorScheme.onPrimary,
        shape = MaterialShapes.Pill,
        shapeColor = colorScheme.primary,
        action = { navController.navigate(ImportExportRoute) }
    ),
    MoreBean(
        title = context.getString(R.string.mini_tool_screen_name),
        icon = Icons.Outlined.Extension,
        iconTint = colorScheme.onSecondary,
        shape = MaterialShapes.Clover8Leaf,
        shapeColor = colorScheme.secondary,
        action = { navController.navigate(MiniToolRoute) }
    ),
    MoreBean(
        title = context.getString(R.string.settings),
        icon = Icons.Outlined.Settings,
        iconTint = colorScheme.onTertiary,
        shape = MaterialShapes.Clover4Leaf,
        shapeColor = colorScheme.tertiary,
        action = { navController.navigate(SettingsRoute) }
    ),
    MoreBean(
        title = context.getString(R.string.about),
        icon = Icons.Outlined.Info,
        iconTint = colorScheme.onPrimary,
        shape = MaterialShapes.Cookie12Sided,
        shapeColor = colorScheme.primary,
        action = { navController.navigate(AboutRoute) }
    )
)