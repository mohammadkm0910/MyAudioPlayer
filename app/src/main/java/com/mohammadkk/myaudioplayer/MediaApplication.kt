package com.mohammadkk.myaudioplayer

import android.app.Application
import android.os.Build
import android.app.NotificationChannel
import com.mohammadkk.myaudioplayer.MediaApplication
import android.app.NotificationManager

class MediaApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        createChannelNotification()
    }
    private fun createChannelNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val musicChannel = NotificationChannel(MUSIC_CHANNEL, "music channel", NotificationManager.IMPORTANCE_HIGH)
            musicChannel.description = "notification controller music"
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(musicChannel)
        }
    }
    companion object {
        const val MUSIC_CHANNEL = "music action"
        const val ACTION_PREVIOUS = "actionprevious"
        const val ACTION_NEXT = "actionnext"
        const val ACTION_PLAY = "actionplay"
        const val ACTION_STOP_SERVICE = "actionstopservice"
    }
}