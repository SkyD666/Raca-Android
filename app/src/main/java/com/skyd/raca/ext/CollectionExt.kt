package com.skyd.raca.ext

inline fun <T> MutableCollection<T>.addIfAny(data: T, predicate: (T) -> Boolean) {
    if (find { !predicate(it) } == null) {
        this += data
    }
}