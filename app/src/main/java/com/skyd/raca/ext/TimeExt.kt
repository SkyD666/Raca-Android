package com.skyd.raca.ext

import java.text.SimpleDateFormat
import java.util.*

fun dateTime(timestamp: Long): String {
    return try {
        SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault())
            .format(Date(timestamp))
    } catch (e: Exception) {
        ""
    }
}