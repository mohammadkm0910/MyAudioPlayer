package com.mohammadkk.myaudioplayer

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import androidx.annotation.RequiresApi
import com.mohammadkk.myaudioplayer.helper.BuildUtil

val buildCacheApp: CacheApp by lazy { AudioApp.cacheApp }

class AudioApp : Application() {
    override fun onCreate() {
        super.onCreate()
        cacheApp = CacheApp(applicationContext)
        if (BuildUtil.isOreoPlus()) {
            createChannelNotification()
        }
    }
    @RequiresApi(Build.VERSION_CODES.O)
    private fun createChannelNotification() {
        val importance = NotificationManager.IMPORTANCE_HIGH
        val audioChannel = NotificationChannel(AUDIO_CHANNEL, "audio channel", importance)
        audioChannel.description = "notification controller music"
        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager.createNotificationChannel(audioChannel)
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