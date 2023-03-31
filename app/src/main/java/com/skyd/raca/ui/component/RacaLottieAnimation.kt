package com.skyd.raca.ui.component

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.airbnb.lottie.compose.*

@Composable
fun RacaLottieAnimation(modifier: Modifier = Modifier, @androidx.annotation.RawRes resId: Int) {
    val lottieComposition by rememberLottieComposition(LottieCompositionSpec.RawRes(resId))
    val lottieProgress by animateLottieCompositionAsState(
        composition = lottieComposition,
        iterations = LottieConstants.IterateForever
    )
    LottieAnimation(
        modifier = modifier,
        composition = lottieComposition,
        progress = { lottieProgress },
    )
}