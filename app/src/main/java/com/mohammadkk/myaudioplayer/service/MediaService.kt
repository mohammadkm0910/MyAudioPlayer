package com.mohammadkk.myaudioplayer.service

import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.graphics.BitmapFactory
import android.media.MediaMetadata
import android.media.MediaPlayer
import android.os.Binder
import android.os.IBinder
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import androidx.core.app.NotificationCompat
import com.mohammadkk.myaudioplayer.AudioApp
import com.mohammadkk.myaudioplayer.PlayerActivity
import com.mohammadkk.myaudioplayer.R
import com.mohammadkk.myaudioplayer.buildCacheApp
import com.mohammadkk.myaudioplayer.extension.getCoverTrack
import com.mohammadkk.myaudioplayer.extension.toContentUri
import com.mohammadkk.myaudioplayer.fragment.NowPlayerFragment
import com.mohammadkk.myaudioplayer.model.Track
import java.io.IOException

class MediaService : Service() {
    private var bindService: IBinder = BindService()
    private var mediaPlayer: MediaPlayer? = null
    private var mediaSessionCompat: MediaSessionCompat? = null
    private var callBackService: CallBackService? = null

    private var servicePosition = 0

    override fun onCreate() {
        super.onCreate()
        mediaPlayer = MediaPlayer()
        mediaSessionCompat = MediaSessionCompat(baseContext, "My Media Player")
        isExists = true
    }
    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer!!.stop()
        mediaPlayer!!.release()
        isExists = false
        buildCacheApp.requirClear()
    }
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val list = buildCacheApp.getStoreTracks()
        if (list != null) {
            mediaList.clear()
            mediaList.addAll(list)
        } else stopSelf()
        var pos = buildCacheApp.globalTrackIndexCaller
        pos = if (pos <= mediaList.size && pos >= 0) pos else 0
        servicePosition = pos
        val actionName = intent?.getStringExtra("actionName")
        mediaPlayer!!.setOnCompletionListener {
            callBackService!!.setNextMusic()
        }
        when (actionName ?: "") {
            "playPause" -> callBackService!!.setPlayPauseMusic()
            "next" -> callBackService!!.setNextMusic()
            "previous" -> callBackService!!.setPrevMusic()
            "stopService" -> {
                stopForeground(true)
                stopSelf()
            }
        }
        return START_STICKY
    }
    fun setCallBackService(callBackService: CallBackService?) {
        this.callBackService = callBackService
    }

    fun reset() {
        mediaPlayer!!.reset()
    }

    @Throws(IOException::class)
    fun createMediaPlayer(position: Int) {
        mediaPlayer!!.setDataSource(this, mediaList[servicePosition].id.toContentUri())
        servicePosition = position
        buildCacheApp.globalTrackIndexCaller = position
        val builder = MediaMetadataCompat.Builder()
            .putString(MediaMetadata.METADATA_KEY_TITLE, mediaList[servicePosition].title)
            .putString(
                MediaMetadata.METADATA_KEY_ARTIST,
                mediaList[servicePosition].artist
            )
            .build()
        mediaSessionCompat!!.setMetadata(builder)
    }
    @Throws(IOException::class)
    fun prepare() {
        mediaPlayer!!.prepare()
    }
    fun start() {
        mediaPlayer!!.start()
    }
    val isPlaying: Boolean
        get() = mediaPlayer!!.isPlaying
    val currentPosition: Int
        get() = mediaPlayer!!.currentPosition
    val duration: Int
        get() = mediaPlayer!!.duration

    fun seekTo(mSec: Int) {
        mediaPlayer!!.seekTo(mSec)
    }

    fun pause() {
        mediaPlayer!!.pause()
    }

    fun showNotification(playImg: Int) {
        val intent = Intent(this, PlayerActivity::class.java)
        val contentIntent = PendingIntent.getActivity(this, 0, intent, 0)
        val prevIntent = Intent(this, NotificationReceiver::class.java).setAction(AudioApp.ACTION_PREVIOUS)
        val prevPending = PendingIntent.getBroadcast(this, 0, prevIntent, PendingIntent.FLAG_UPDATE_CURRENT)
        val pauseIntent = Intent(this, NotificationReceiver::class.java).setAction(AudioApp.ACTION_PLAY)
        val pausePending = PendingIntent.getBroadcast(this, 0, pauseIntent, PendingIntent.FLAG_UPDATE_CURRENT)
        val nextIntent = Intent(this, NotificationReceiver::class.java).setAction(AudioApp.ACTION_NEXT)
        val nextPending = PendingIntent.getBroadcast(this, 0, nextIntent, PendingIntent.FLAG_UPDATE_CURRENT)
        val thumbInit = baseContext.getCoverTrack(mediaList[servicePosition].id.toContentUri())
        val thumb = thumbInit ?: BitmapFactory.decodeResource(resources, R.drawable.ic_music_large)
        buildCacheApp.iconPausedCaller = playImg
        buildCacheApp.titleTextCaller = mediaList[servicePosition].title
        buildCacheApp.artistTextCaller = mediaList[servicePosition].artist
        NowPlayerFragment.listener?.updateNowPlay(playImg, mediaList[servicePosition])
        val notification = NotificationCompat.Builder(this, AudioApp.AUDIO_CHANNEL)
            .setSmallIcon(R.drawable.ic_track)
            .setLargeIcon(thumb)
            .setContentTitle(mediaList[servicePosition].title)
            .setContentText(mediaList[servicePosition].artist)
            .setContentIntent(contentIntent)
            .addAction(R.drawable.ic_skip_previous, "Previous", prevPending)
            .addAction(playImg, "Pause", pausePending)
            .addAction(R.drawable.ic_skip_next, "Next", nextPending)
            .setStyle(
                androidx.media.app.NotificationCompat.MediaStyle().setMediaSession(
                    mediaSessionCompat!!.sessionToken
                ).setShowActionsInCompactView(0, 1, 2)
            )
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setOnlyAlertOnce(true)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .build()
        startForeground(1, notification)
    }

    override fun onBind(intent: Intent): IBinder {
        return bindService
    }

    inner class BindService : Binder() {
        val service: MediaService
            get() = this@MediaService
    }
    companion object {
        private var isExists: Boolean = false
        fun getIsExists() = isExists
        private var mediaList = ArrayList<Track>()
        @JvmName("getMediaList1")
        fun getMediaList(): ArrayList<Track> = mediaList
    }
}