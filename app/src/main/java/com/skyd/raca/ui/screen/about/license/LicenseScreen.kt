package com.skyd.raca.ui.screen.about.license

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.skyd.raca.R
import com.skyd.raca.ext.plus
import com.skyd.raca.ext.safeOpenUri
import com.skyd.raca.model.bean.LicenseBean
import com.skyd.raca.ui.component.RacaTopBar
import com.skyd.raca.ui.component.RacaTopBarStyle
import kotlinx.serialization.Serializable

@Serializable
data object LicenseRoute

@Composable
fun LicenseScreen() {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    Scaffold(
        topBar = {
            RacaTopBar(
                style = RacaTopBarStyle.LargeFlexible,
                title = { Text(text = stringResource(R.string.license_screen_name)) },
                scrollBehavior = scrollBehavior,
            )
        }
    ) {
        val dataList = remember { getLicenseList() }
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .nestedScroll(scrollBehavior.nestedScrollConnection),
            contentPadding = PaddingValues(vertical = 7.dp) + it,
        ) {
            items(items = dataList) { item ->
                LicenseItem(item)
            }
        }
    }
}

@Composable
private fun LicenseItem(data: LicenseBean) {
    Card(
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 7.dp),
        shape = RoundedCornerShape(20.dp)
    ) {
        val uriHandler = LocalUriHandler.current
        Column(
            modifier = Modifier
                .clickable { uriHandler.safeOpenUri(data.link) }
                .padding(15.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    modifier = Modifier.weight(1f),
                    text = data.name,
                    style = MaterialTheme.typography.titleLarge
                )
                Text(
                    modifier = Modifier.padding(start = 5.dp),
                    text = data.license,
                    style = MaterialTheme.typography.labelMedium
                )
            }
            Text(
                modifier = Modifier.padding(top = 6.dp),
                text = data.link,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

private fun getLicenseList(): List<LicenseBean> {
    return listOf(
        LicenseBean(
            name = "Android Open Source Project",
            license = "Apache-2.0",
            link = "https://source.android.com/"
        ),
        LicenseBean(
            name = "Accompanist",
            license = "Apache-2.0",
            link = "https://github.com/google/accompanist"
        ),
        LicenseBean(
            name = "Koin",
            license = "Apache-2.0",
            link = "https://github.com/InsertKoinIO/koin"
        ),
        LicenseBean(
            name = "Coil",
            license = "Apache-2.0",
            link = "https://github.com/coil-kt/coil"
        ),
        LicenseBean(
            name = "kotlinx.coroutines",
            license = "Apache-2.0",
            link = "https://github.com/Kotlin/kotlinx.coroutines"
        ),
        LicenseBean(
            name = "kotlin-csv",
            license = "Apache-2.0",
            link = "https://github.com/doyaaaaaken/kotlin-csv"
        ),
        LicenseBean(
            name = "sardine-android",
            license = "Apache-2.0",
            link = "https://github.com/thegrizzlylabs/sardine-android"
        ),
        LicenseBean(
            name = "kotlinx.serialization",
            license = "Apache-2.0",
            link = "https://github.com/Kotlin/kotlinx.serialization"
        ),
        LicenseBean(
            name = "MaterialKolor",
            license = "MIT",
            link = "https://github.com/jordond/MaterialKolor"
        ),
        LicenseBean(
            name = "Read You",
            license = "GPL-3.0",
            link = "https://github.com/Ashinch/ReadYou"
        ),
        LicenseBean(
            name = "Compottie",
            license = "MIT",
            link = "https://github.com/alexzhirkevich/compottie"
        ),
        LicenseBean(
            name = "Jpinyin",
            license = "GPL-3.0",
            link = "https://mvnrepository.com/artifact/com.github.stuxuhai/jpinyin"
        ),
    )
}