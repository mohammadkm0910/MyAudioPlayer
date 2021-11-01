package com.mohammadkk.myaudioplayer

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.mohammadkk.myaudioplayer.service.MediaService
import com.mohammadkk.myaudioplayer.MediaApplication
import androidx.core.content.ContextCompat

class NotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val actionName = intent?.action
        val intentService = Intent(context, MediaService::class.java)
        if (actionName != null && context != null) {
            when (actionName) {
                MediaApplication.ACTION_PLAY -> {
                    intentService.putExtra("actionName", "playPause")
                    ContextCompat.startForegroundService(context, intentService)
                }
                MediaApplication.ACTION_NEXT -> {
                    intentService.putExtra("actionName", "next")
                    ContextCompat.startForegroundService(context, intentService)
                }
                MediaApplication.ACTION_PREVIOUS -> {
                    intentService.putExtra("actionName", "previous")
                    ContextCompat.startForegroundService(context, intentService)
                }
                MediaApplication.ACTION_STOP_SERVICE -> {
                    intentService.putExtra("actionName", "stopService")
                    ContextCompat.startForegroundService(context, intentService)
                }
            }
        }
    }
}