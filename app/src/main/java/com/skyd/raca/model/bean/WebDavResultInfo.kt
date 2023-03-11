package com.skyd.raca.model.bean

import kotlinx.serialization.Serializable

@Serializable
data class WebDavResultInfo(
    var time: Long,
    var count: Int,
) : BaseBean
