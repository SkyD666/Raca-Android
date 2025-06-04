package com.skyd.raca.ui.component

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalResources
import io.github.alexzhirkevich.compottie.Compottie
import io.github.alexzhirkevich.compottie.LottieCompositionSpec
import io.github.alexzhirkevich.compottie.rememberLottieComposition
import io.github.alexzhirkevich.compottie.rememberLottiePainter

@Composable
fun RacaLottieAnimation(
    modifier: Modifier = Modifier,
    @androidx.annotation.RawRes resId: Int,
    contentScale: ContentScale = ContentScale.Inside,
) {
    val resources = LocalResources.current
    val composition by rememberLottieComposition {
        LottieCompositionSpec.JsonString(
            resources.openRawResource(resId).bufferedReader().use { it.readText() }
        )
    }

    Image(
        painter = rememberLottiePainter(
            composition = composition,
            iterations = Compottie.IterateForever
        ),
        contentDescription = null,
        modifier = modifier,
        contentScale = contentScale,
    )
}