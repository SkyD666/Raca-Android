package com.skyd.raca.ui.screen.minitool

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEmotions
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.skyd.raca.R
import com.skyd.raca.model.bean.MiniTool1Bean
import com.skyd.raca.ui.component.RacaTopBar
import com.skyd.raca.ui.component.lazyverticalgrid.RacaLazyVerticalGrid
import com.skyd.raca.ui.component.lazyverticalgrid.adapter.LazyGridAdapter
import com.skyd.raca.ui.component.lazyverticalgrid.adapter.proxy.MiniTool1Proxy
import com.skyd.raca.ui.local.LocalNavController
import com.skyd.raca.ui.screen.minitool.abstractemoji.ABSTRACT_EMOJI_SCREEN_ROUTE

const val MINI_TOOL_SCREEN_ROUTE = "miniToolScreen"

@Composable
fun MiniToolScreen() {
    val navController = LocalNavController.current

    Scaffold(
        topBar = {
            RacaTopBar(title = { Text(text = stringResource(id = R.string.mini_tool_screen_name)) })
        }
    ) {
        val miniToolList = listOf(
            MiniTool1Bean(
                title = stringResource(R.string.abstract_emoji_screen_name),
                icon = Icons.Default.EmojiEmotions,
                action = { navController.navigate(ABSTRACT_EMOJI_SCREEN_ROUTE) }
            ),
        )

        val adapter = remember {
            LazyGridAdapter(
                mutableListOf(MiniTool1Proxy(onClickListener = { data -> data.action.invoke() }))
            )
        }
        RacaLazyVerticalGrid(
            modifier = Modifier.fillMaxSize(),
            dataList = miniToolList,
            adapter = adapter,
            contentPadding = it
        )
    }
}