package com.mohammadkk.myaudioplayer.helper

import android.os.Build
import android.os.Looper

object BuildUtil {
    fun isRPlus() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.R
    fun isQPlus() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q
    fun isOreoPlus() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
    fun isMarshmallowPlus() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M

    fun isOnMainThread() = Looper.myLooper() == Looper.getMainLooper()

    fun buildBaseThread(callback : () -> Unit) {
        if (isOnMainThread()) {
            Thread {
                callback()
            }.start()
        } else callback()
    }
}