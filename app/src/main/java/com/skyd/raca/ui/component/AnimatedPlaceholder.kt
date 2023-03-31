package com.skyd.raca.ui.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun AnimatedPlaceholder(@androidx.annotation.RawRes resId: Int, tip: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            modifier = Modifier.fillMaxSize(fraction = 0.5f),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            RacaLottieAnimation(
                modifier = Modifier.weight(1f),
                resId = resId,
            )
            Text(
                modifier = Modifier.padding(top = 5.dp),
                text = tip,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.labelLarge
            )
        }
    }
}