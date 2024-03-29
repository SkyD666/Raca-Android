package com.skyd.raca.ui.component.lazyverticalgrid.adapter.proxy

import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ClipboardManager
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.skyd.raca.model.bean.ArticleWithTags1
import com.skyd.raca.ui.component.RacaCard
import com.skyd.raca.ui.component.lazyverticalgrid.adapter.LazyGridAdapter

class ArticleWithTags1Proxy(
    private val onClickListener: ((data: ArticleWithTags1) -> Unit)? = null
) : LazyGridAdapter.Proxy<ArticleWithTags1>() {
    @Composable
    override fun Draw(modifier: Modifier, index: Int, data: ArticleWithTags1) {
        ArticleWithTags1Item(modifier = modifier, data = data, onClickListener = onClickListener)
    }
}

@Composable
fun ArticleWithTags1Item(
    modifier: Modifier = Modifier,
    data: ArticleWithTags1,
    onClickListener: ((data: ArticleWithTags1) -> Unit)? = null
) {
    val clipboardManager: ClipboardManager = LocalClipboardManager.current

    RacaCard(
        modifier = modifier,
        colors = CardDefaults.cardColors(Color.Transparent),
        onLongClick = { clipboardManager.setText(AnnotatedString(data.article.article)) },
        onClick = { onClickListener?.invoke(data) }
    ) {
        if (data.article.title.isNotBlank()) {
            Text(
                modifier = Modifier
                    .padding(top = 12.dp)
                    .padding(horizontal = 10.dp)
                    .basicMarquee(iterations = Int.MAX_VALUE),
                text = data.article.title,
                style = MaterialTheme.typography.titleLarge,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1
            )
        }
        Text(
            modifier = Modifier
                .padding(
                    top = if (data.article.title.isNotBlank()) 6.dp else 12.dp,
                    bottom = 12.dp
                )
                .padding(horizontal = 10.dp),
            text = data.article.article,
            style = MaterialTheme.typography.bodyMedium,
            overflow = TextOverflow.Ellipsis,
            maxLines = 3
        )
    }
}
