package com.mohammadkk.myaudioplayer.service;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadata;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaSessionCompat;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.mohammadkk.myaudioplayer.NotificationReceiver;
import com.mohammadkk.myaudioplayer.PlayerActivity;
import com.mohammadkk.myaudioplayer.R;
import com.mohammadkk.myaudioplayer.helper.MusicUtil;
import com.mohammadkk.myaudioplayer.model.Songs;

import java.io.IOException;
import java.util.ArrayList;
import static com.mohammadkk.myaudioplayer.MainActivity.songsAllList;
import static com.mohammadkk.myaudioplayer.MediaApplication.ACTION_NEXT;
import static com.mohammadkk.myaudioplayer.MediaApplication.ACTION_PLAY;
import static com.mohammadkk.myaudioplayer.MediaApplication.ACTION_PREVIOUS;
import static com.mohammadkk.myaudioplayer.MediaApplication.MUSIC_CHANNEL;

public class MediaService extends Service {

    IBinder bindService = new BindService();
    public static boolean isService = false;
    private static int servicePosition = 0;
    private ArrayList<Songs> mediaList = new ArrayList<>();
    private MediaPlayer mediaPlayer;
    private MediaSessionCompat mediaSessionCompat;
    private CallBackService callBackService;

    @Override
    public void onCreate() {
        super.onCreate();
        mediaList = songsAllList;
        mediaPlayer = new MediaPlayer();
        mediaSessionCompat = new MediaSessionCompat(getBaseContext(),"My Media Player");
        isService = true;
    }

    public ArrayList<Songs> getMediaList() {
        return mediaList;
    }
    public void setMediaList(ArrayList<Songs> mediaList) {
        this.mediaList = mediaList;
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        mediaPlayer.stop();
        isService = false;
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String actionName = intent.getStringExtra("actionName");
        mediaPlayer.setOnCompletionListener(mp -> callBackService.setNextMusic());
        if (actionName != null) {
            switch (actionName) {
                case "playPause":
                    callBackService.setPlayPauseMusic();
                    break;
                case "next":
                    callBackService.setNextMusic();
                    break;
                case "previous":
                    callBackService.setPrevMusic();
                    break;
                case "stopService":
                    stopSelf();
                    break;
            }
        }
        return START_STICKY;
    }
    public void setCallBackService(CallBackService callBackService) {
        this.callBackService = callBackService;
    }
    public void reset() {
        mediaPlayer.reset();
    }
    public void createMediaPlayer(int position) throws IOException {
        mediaPlayer.setDataSource(this, MusicUtil.getUriPath(mediaList.get(position)));
        servicePosition = position;
        MediaMetadataCompat builder = new MediaMetadataCompat.Builder()
                .putString(MediaMetadata.METADATA_KEY_TITLE, mediaList.get(servicePosition).getTitle())
                .putString(MediaMetadata.METADATA_KEY_ARTIST, mediaList.get(servicePosition).getArtist())
                .build();
        mediaSessionCompat.setMetadata(builder);
    }
    public int getServicePosition() {
        return servicePosition;
    }
    public void prepare() throws IOException {
        mediaPlayer.prepare();
    }
    public void start() {
        mediaPlayer.start();
    }
    public boolean isPlaying() {
        return mediaPlayer.isPlaying();
    }
    public int getCurrentPosition() {
        return mediaPlayer.getCurrentPosition();
    }
    public int getDuration() {
        return mediaPlayer.getDuration();
    }
    public void seekTo(int mSec) {
        mediaPlayer.seekTo(mSec);
    }
    public void pause() {
        mediaPlayer.pause();
    }
    public void showNotification(int playImg) {
        Intent intent = new Intent(this, PlayerActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, intent, 0);
        Intent prevIntent = new Intent(this, NotificationReceiver.class).setAction(ACTION_PREVIOUS);
        PendingIntent prevPending = PendingIntent.getBroadcast(this, 0, prevIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        Intent pauseIntent = new Intent(this, NotificationReceiver.class).setAction(ACTION_PLAY);
        PendingIntent pausePending = PendingIntent.getBroadcast(this, 0, pauseIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        Intent nextIntent = new Intent(this, NotificationReceiver.class).setAction(ACTION_NEXT);
        PendingIntent nextPending = PendingIntent.getBroadcast(this, 0, nextIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        Bitmap thumbInit = MusicUtil.getSongCover(getBaseContext(), MusicUtil.getUriPath(mediaList.get(servicePosition)));
        Bitmap thumb = thumbInit != null ? thumbInit : BitmapFactory.decodeResource(getResources(), R.drawable.ic_music_large);
        SharedPreferences pref = getSharedPreferences("cache_service", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt("play_state", playImg);
        editor.putInt("play_pos", servicePosition);
        editor.apply();
        Notification notification =  new NotificationCompat.Builder(this, MUSIC_CHANNEL)
                .setSmallIcon(R.drawable.ic_songs)
                .setLargeIcon(thumb)
                .setContentTitle(mediaList.get(servicePosition).getTitle())
                .setContentText(mediaList.get(servicePosition).getArtist())
                .setContentIntent(contentIntent)
                .addAction(R.drawable.ic_skip_prev, "Previous", prevPending)
                .addAction(playImg, "Pause", pausePending)
                .addAction(R.drawable.ic_skip_next, "Next", nextPending)
                .setStyle(new androidx.media.app.NotificationCompat.MediaStyle().setMediaSession(mediaSessionCompat.getSessionToken()).setShowActionsInCompactView(0, 1, 2))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setOnlyAlertOnce(true)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .build();
        startForeground(1, notification);
    }
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return bindService;
    }
    public class BindService extends Binder {
        public MediaService getService() {
            return MediaService.this;
        }
    }
}
