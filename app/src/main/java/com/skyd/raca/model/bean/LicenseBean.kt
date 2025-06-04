package com.skyd.raca.model.bean

import kotlinx.serialization.Serializable

@Serializable
class LicenseBean(
    val name: String,
    val license: String,
    val link: String
) : BaseBean