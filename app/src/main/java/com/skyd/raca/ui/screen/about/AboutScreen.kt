package com.skyd.raca.ui.screen.about

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Balance
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.toShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.skyd.raca.R
import com.skyd.raca.config.Const
import com.skyd.raca.ext.isCompact
import com.skyd.raca.ext.plus
import com.skyd.raca.ext.safeOpenUri
import com.skyd.raca.model.bean.OtherWorksBean
import com.skyd.raca.ui.component.RacaIconButton
import com.skyd.raca.ui.component.RacaTopBar
import com.skyd.raca.ui.component.RacaTopBarStyle
import com.skyd.raca.ui.local.LocalNavController
import com.skyd.raca.ui.local.LocalWindowSizeClass
import com.skyd.raca.ui.screen.about.license.LicenseRoute
import com.skyd.raca.util.CommonUtil
import com.skyd.raca.util.CommonUtil.openBrowser
import kotlinx.serialization.Serializable

@Serializable
data object AboutRoute

@Composable
fun AboutScreen() {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val navController = LocalNavController.current

    Scaffold(
        topBar = {
            RacaTopBar(
                style = RacaTopBarStyle.LargeFlexible,
                scrollBehavior = scrollBehavior,
                title = { Text(text = stringResource(R.string.about)) },
                actions = {
                    RacaIconButton(
                        imageVector = Icons.Outlined.Balance,
                        contentDescription = stringResource(R.string.license_screen_name),
                        onClick = { navController.navigate(LicenseRoute) }
                    )
                },
            )
        }
    ) { paddingValues ->
        val windowSizeClass = LocalWindowSizeClass.current
        val otherWorksList = rememberOtherWorksList()

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .nestedScroll(scrollBehavior.nestedScrollConnection),
            contentPadding = paddingValues + PaddingValues(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (windowSizeClass.isCompact) {
                item { IconArea() }
                item { TextArea() }
                item { ButtonArea() }
            } else {
                item {
                    Row(modifier = Modifier.fillMaxWidth()) {
                        Column(
                            modifier = Modifier.weight(0.95f),
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            IconArea()
                            ButtonArea()
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        TextArea(modifier = Modifier.weight(1f))
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                }
            }

            item {
                Text(
                    text = stringResource(R.string.about_screen_other_works),
                    style = MaterialTheme.typography.titleMedium,
                )
            }
            itemsIndexed(items = otherWorksList) { _, item ->
                OtherWorksItem(data = item)
            }
        }
    }
}

@Composable
private fun IconArea() {
    Image(
        modifier = Modifier
            .padding(20.dp)
            .fillMaxWidth(0.4f)
            .aspectRatio(1f),
        contentScale = ContentScale.Crop,
        alignment = Alignment.Center,
        painter = painterResource(id = R.drawable.ic_raca),
        contentDescription = null
    )
}

@Composable
private fun TextArea(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxWidth(1f),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        BadgedBox(
            badge = {
                Badge {
                    val badgeNumber = remember { CommonUtil.getAppVersionName() }
                    Text(
                        badgeNumber,
                        modifier = Modifier.semantics { contentDescription = badgeNumber }
                    )
                }
            }
        ) {
            Text(
                text = stringResource(id = R.string.app_name),
                style = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Center
            )
        }
        Text(
            text = stringResource(id = R.string.about_screen_app_full_name),
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center
        )
        Card(
            modifier = Modifier.padding(top = 20.dp),
            shape = RoundedCornerShape(10)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(id = R.string.about_screen_description_1),
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center
                )
                Text(
                    modifier = Modifier.padding(top = 16.dp),
                    text = stringResource(id = R.string.about_screen_description_2),
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center
                )
                Text(
                    modifier = Modifier.padding(top = 16.dp),
                    text = stringResource(id = R.string.about_screen_description_3),
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
private fun ButtonArea() {
    val uriHandler = LocalUriHandler.current
    Row(
        modifier = Modifier.padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        val boxModifier = Modifier.padding(vertical = 16.dp, horizontal = 6.dp)
        Box(
            modifier = boxModifier.background(
                color = MaterialTheme.colorScheme.primaryContainer,
                shape = MaterialShapes.Cookie9Sided.toShape(),
            ),
            contentAlignment = Alignment.Center
        ) {
            RacaIconButton(
                painter = painterResource(R.drawable.ic_github_24),
                contentDescription = stringResource(R.string.about_screen_visit_github),
                onClick = { uriHandler.safeOpenUri(Const.GITHUB_REPO) }
            )
        }
        Box(
            modifier = boxModifier.background(
                color = MaterialTheme.colorScheme.secondaryContainer,
                shape = MaterialShapes.Pill.toShape(),
            ),
            contentAlignment = Alignment.Center
        ) {
            RacaIconButton(
                painter = painterResource(R.drawable.ic_telegram_24),
                contentDescription = stringResource(R.string.about_screen_join_telegram),
                onClick = { uriHandler.safeOpenUri(Const.TELEGRAM_GROUP) }
            )
        }
        Box(
            modifier = boxModifier.background(
                color = MaterialTheme.colorScheme.tertiaryContainer,
                shape = MaterialShapes.Clover4Leaf.toShape(),
            ),
            contentAlignment = Alignment.Center
        ) {
            RacaIconButton(
                painter = painterResource(R.drawable.ic_discord_24),
                contentDescription = stringResource(R.string.about_screen_join_discord),
                onClick = { uriHandler.safeOpenUri(Const.DISCORD_SERVER) }
            )
        }
    }
}

@Composable
private fun rememberOtherWorksList(): List<OtherWorksBean> {
    val context = LocalContext.current
    return remember {
        listOf(
            OtherWorksBean(
                name = context.getString(R.string.about_screen_other_works_rays_name),
                icon = R.drawable.ic_rays,
                description = context.getString(R.string.about_screen_other_works_rays_description),
                url = context.getString(R.string.about_screen_other_works_rays_url)
            ),
            OtherWorksBean(
                name = context.getString(R.string.about_screen_other_works_night_screen_name),
                icon = R.drawable.ic_night_screen,
                description = context.getString(R.string.about_screen_other_works_night_screen_description),
                url = context.getString(R.string.about_screen_other_works_night_screen_url)
            ),
        )
    }
}

@Composable
private fun OtherWorksItem(
    modifier: Modifier = Modifier,
    data: OtherWorksBean,
) {
    Card(
        modifier = modifier
            .padding(vertical = 10.dp)
            .fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .clickable { openBrowser(data.url) }
                .padding(15.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                AsyncImage(
                    modifier = Modifier
                        .size(30.dp)
                        .aspectRatio(1f),
                    model = data.icon,
                    contentDescription = data.name
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = data.name,
                    style = MaterialTheme.typography.titleLarge
                )
            }
            Text(
                modifier = Modifier.padding(top = 6.dp),
                text = data.description,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}