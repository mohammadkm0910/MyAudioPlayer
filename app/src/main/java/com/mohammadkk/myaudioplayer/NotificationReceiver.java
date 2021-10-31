package com.mohammadkk.myaudioplayer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.content.ContextCompat;

import com.mohammadkk.myaudioplayer.service.MediaService;

import static com.mohammadkk.myaudioplayer.MediaApplication.ACTION_NEXT;
import static com.mohammadkk.myaudioplayer.MediaApplication.ACTION_PLAY;
import static com.mohammadkk.myaudioplayer.MediaApplication.ACTION_PREVIOUS;
import static com.mohammadkk.myaudioplayer.MediaApplication.ACTION_STOP_SERVICE;

public class NotificationReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String actionName = intent.getAction();
        Intent intentService = new Intent(context, MediaService.class);
        if (actionName != null && context != null) {
            switch (actionName) {
                case ACTION_PLAY:
                    intentService.putExtra("actionName", "playPause");
                    ContextCompat.startForegroundService(context, intentService);
                    break;
                case ACTION_NEXT:
                    intentService.putExtra("actionName", "next");
                    ContextCompat.startForegroundService(context, intentService);
                    break;
                case ACTION_PREVIOUS:
                    intentService.putExtra("actionName", "previous");
                    ContextCompat.startForegroundService(context, intentService);
                    break;
                case ACTION_STOP_SERVICE:
                    intentService.putExtra("actionName", "stopService");
                    ContextCompat.startForegroundService(context, intentService);
                    break;
            }
        }
    }
}
