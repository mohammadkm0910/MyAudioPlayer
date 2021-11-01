package com.mohammadkk.myaudioplayer.helper

import java.util.*

fun Int.formatTimeMusic(): String {
    val h = this / (1000 * 60 * 60) % 24
    val m = this / (1000 * 60) % 60
    val s = this / 1000 % 60
    return if (h > 0) {
        String.format(Locale.ENGLISH, "%02d:%02d:%02d", h, m, s)
    } else {
        String.format(Locale.ENGLISH, "%02d:%02d", m, s)
    }
}