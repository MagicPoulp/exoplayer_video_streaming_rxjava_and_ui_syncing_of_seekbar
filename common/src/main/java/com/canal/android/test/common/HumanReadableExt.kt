package com.canal.android.test.common

import kotlin.math.log10
import kotlin.math.pow

// https://stackoverflow.com/questions/63966076/dependency-to-format-values-to-human-readable-form-in-kotlin
val Long.formatHumanReadable: String
    get() = log10(coerceAtLeast(1).toDouble()).toInt().div(3).let {
        val precision = when (it) {
            0 -> 0; else -> 1
        }
        val suffix = arrayOf("", "K", "M", "G", "T", "P", "E", "Z", "Y")
        String.format("%.${precision}f ${suffix[it]}", toDouble() / 10.0.pow(it * 3))
    }

fun Long.msFormatTimeHumanReadable() : String {
    val allSeconds = this / 1000
    val s = allSeconds % 60
    val min = (allSeconds / 60) % 60
    val h = (allSeconds / 60) / 60
    var result = ""
    if (h > 0) {
        result += h.toString() + "h"
    }
    if (h > 0 || min > 0) {
        if (h > 0) {
            result += " "
        }
        result += min.toString() + "m"
        result += " "
    }
    result += s.toString() + "s"
    return result
}
