package com.skyd.raca.ui.screen.minitool

import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.EmojiEmotions
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.skyd.raca.R
import com.skyd.raca.model.bean.MiniTool1Bean
import com.skyd.raca.ui.component.RacaTopBar
import com.skyd.raca.ui.local.LocalNavController
import com.skyd.raca.ui.screen.minitool.abstractemoji.AbstractEmojiRoute
import kotlinx.serialization.Serializable

@Serializable
data object MiniToolRoute

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
                icon = Icons.Outlined.EmojiEmotions,
                action = { navController.navigate(AbstractEmojiRoute) }
            ),
        )
        LazyVerticalGrid(
            columns = GridCells.Adaptive(500.dp),
            modifier = Modifier.fillMaxSize(),
            contentPadding = it
        ) {
            items(miniToolList) { item ->
                MiniToolItem(
                    data = item,
                    onClickListener = { data -> data.action.invoke() },
                )
            }
        }
    }
}

@Composable
private fun MiniToolItem(
    data: MiniTool1Bean,
    onClickListener: ((data: MiniTool1Bean) -> Unit)? = null
) {
    OutlinedCard(
        modifier = Modifier.padding(vertical = 6.dp),
        shape = RoundedCornerShape(16)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .combinedClickable(
                    onClick = {
                        onClickListener?.invoke(data)
                    }
                )
                .padding(25.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                modifier = Modifier.size(35.dp),
                imageVector = data.icon,
                contentDescription = null,
            )
            Text(
                modifier = Modifier
                    .padding(horizontal = 10.dp)
                    .basicMarquee(iterations = Int.MAX_VALUE),
                text = data.title,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 1,
                textAlign = TextAlign.Center
            )
        }
    }
}