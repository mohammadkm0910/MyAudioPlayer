package com.mohammadkk.myaudioplayer

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import com.mohammadkk.myaudioplayer.helper.BuildUtil

val buildCacheApp: CacheApp by lazy { AudioApp.cacheApp }

class AudioApp : Application() {
    override fun onCreate() {
        super.onCreate()
        cacheApp = CacheApp(applicationContext)
        createChannelNotification()
    }
    private fun createChannelNotification() {
        if (BuildUtil.isOreoPlus()) {
            val musicChannel = NotificationChannel(AUDIO_CHANNEL, "music channel", NotificationManager.IMPORTANCE_HIGH)
            musicChannel.description = "notification controller music"
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(musicChannel)
        }
    }
    companion object {
        lateinit var cacheApp: CacheApp
        const val AUDIO_CHANNEL = "audio action"
        const val ACTION_PREVIOUS = "actionprevious"
        const val ACTION_NEXT = "actionnext"
        const val ACTION_PLAY = "actionplay"
        const val ACTION_STOP_SERVICE = "actionstopservice"
    }
}