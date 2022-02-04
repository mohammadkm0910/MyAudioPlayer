package com.mohammadkk.myaudioplayer.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import com.mohammadkk.myaudioplayer.AudioApp

class NotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val actionName = intent?.action
        val intentService = Intent(context, MediaService::class.java)
        if (actionName != null && context != null) {
            when (actionName) {
                AudioApp.ACTION_PLAY -> {
                    intentService.putExtra("actionName", "playPause")
                    ContextCompat.startForegroundService(context, intentService)
                }
                AudioApp.ACTION_NEXT -> {
                    intentService.putExtra("actionName", "next")
                    ContextCompat.startForegroundService(context, intentService)
                }
                AudioApp.ACTION_PREVIOUS -> {
                    intentService.putExtra("actionName", "previous")
                    ContextCompat.startForegroundService(context, intentService)
                }
                AudioApp.ACTION_STOP_SERVICE -> {
                    intentService.putExtra("actionName", "stopService")
                    ContextCompat.startForegroundService(context, intentService)
                }
            }
        }
    }
}