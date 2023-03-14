package com.skyd.raca.config

import kotlinx.coroutines.flow.MutableSharedFlow

val refreshArticleData: MutableSharedFlow<Unit> = MutableSharedFlow(replay = 1, extraBufferCapacity = 1)