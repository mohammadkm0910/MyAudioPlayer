package com.mohammadkk.myaudioplayer

import android.content.Context
import androidx.preference.PreferenceManager

class CacheApp(context: Context) {
    private val prefs = PreferenceManager.getDefaultSharedPreferences(context)
    private val iconPausedServiceKey = "icon_paused_service_pref"
    private val positionServiceKey = "pos_service_pref"

    var iconPausedService: Int
        get() = prefs.getInt(iconPausedServiceKey, R.drawable.ic_pause)
        set(value) = prefs.edit().putInt(iconPausedServiceKey, value).apply()
    var positionService: Int
        get() = prefs.getInt(positionServiceKey, 0)
        set(value) = prefs.edit().putInt(positionServiceKey, value).apply()
}