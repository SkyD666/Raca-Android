package com.skyd.raca.util

import com.skyd.raca.model.bean.ArticleWithTags
import java.math.BigInteger
import java.security.MessageDigest

fun ArticleWithTags.md5(): String {
    val summary = StringBuilder("$article\n")
    tags.forEach {
        summary.append(it).append("\n")
    }
    val md = MessageDigest.getInstance("MD5")
    return BigInteger(1, md.digest(summary.toString().toByteArray()))
        .toString(16).padStart(32, '0')
}