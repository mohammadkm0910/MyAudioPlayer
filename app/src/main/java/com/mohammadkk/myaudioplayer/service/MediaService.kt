package com.mohammadkk.myaudioplayer.service

import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.graphics.BitmapFactory
import android.media.AudioAttributes
import android.media.AudioManager
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
import com.mohammadkk.myaudioplayer.helper.BuildUtil
import com.mohammadkk.myaudioplayer.model.Track
import java.io.IOException

class MediaService : Service(), MediaPlayer.OnCompletionListener, MediaPlayer.OnPreparedListener {
    private var bindService: IBinder = BindService()
    private var mediaPlayer: MediaPlayer? = null
    private var mediaSessionCompat: MediaSessionCompat? = null
    private var listener: ServiceListener? = null
    private var serviceIndex = 0

    override fun onBind(intent: Intent): IBinder {
        return bindService
    }
    override fun onCreate() {
        super.onCreate()
        mediaPlayer = MediaPlayer()
        mediaSessionCompat = MediaSessionCompat(baseContext, getString(R.string.app_name))
        isExists = true
    }
    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.stop()
        mediaPlayer?.release()
        isExists = false
        buildCacheApp.requirClear()
    }
    fun setOnListenerService(listener: ServiceListener) {
        this.listener = listener
    }
    @Throws(IOException::class)
    fun createMediaPlayer(position: Int) {
        if (position >= 1 && position < mediaList.size) {
            serviceIndex = position
            buildCacheApp.globalTrackIndexCaller = serviceIndex
            mediaPlayer?.setDataSource(baseContext, mediaList[position].id.toContentUri())
            val builderInfo = MediaMetadataCompat.Builder()
                .putString(MediaMetadataCompat.METADATA_KEY_TITLE, mediaList[position].title)
                .putString(MediaMetadataCompat.METADATA_KEY_ALBUM, mediaList[position].album)
                .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, mediaList[position].artist)
            mediaSessionCompat?.setMetadata(builderInfo.build())
        }
    }
    override fun onCompletion(mp: MediaPlayer?) {
        listener?.onNextTrack()
    }
    override fun onPrepared(mp: MediaPlayer?) {
        if (mediaPlayer?.isPlaying == false)
            mediaPlayer?.start()
    }
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        serviceIndex = buildCacheApp.globalTrackIndexCaller
        buildCacheApp.getStoreTracks()?.run {
            mediaList = this
        }
        if (serviceIndex == -1 && mediaList.isEmpty()) {
            stopSelf()
        }
        val actionName = intent?.getStringExtra("actionName")

        when (actionName ?: "") {
            "playPause" -> listener?.onPlayPauseTrack()
            "next" -> listener?.onNextTrack()
            "previous" -> listener?.onPreviousTrack()
            "stopService" -> {
                stopForeground(true)
                stopSelf()
            }
        }
        mediaPlayer?.setOnCompletionListener(this)
        mediaPlayer?.setOnPreparedListener(this)
        return START_STICKY
    }
    fun reset() {
        mediaPlayer!!.reset()
    }
    fun setDefaultAudioStream() {
        if (BuildUtil.isOreoPlus()) {
            mediaPlayer?.setAudioAttributes(audioAttributes)
        } else {
            @Suppress("DEPRECATION")
            mediaPlayer?.setAudioStreamType(AudioManager.STREAM_MUSIC)
        }
    }
    @Throws(IOException::class)
    fun prepareAsync() {
        mediaPlayer?.prepareAsync()
    }
    fun start() {
        mediaPlayer!!.start()
    }
    fun seekTo(mSec: Int) {
        mediaPlayer!!.seekTo(mSec)
    }
    fun pause() {
        mediaPlayer!!.pause()
    }
    val isPlaying: Boolean
        get() = mediaPlayer!!.isPlaying
    val currentPosition: Int
        get() = mediaPlayer!!.currentPosition
    val duration: Int
        get() = mediaPlayer!!.duration
    private val audioAttributes = AudioAttributes.Builder()
        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
        .setUsage(AudioAttributes.USAGE_MEDIA)
        .build()
    fun showNotification(playImg: Int) {
        val intent = Intent(this, PlayerActivity::class.java)
        val contentIntent = PendingIntent.getActivity(this, 0, intent, 0)
        val prevIntent = Intent(this, NotificationReceiver::class.java).setAction(AudioApp.ACTION_PREVIOUS)
        val prevPending = PendingIntent.getBroadcast(this, 0, prevIntent, PendingIntent.FLAG_UPDATE_CURRENT)
        val pauseIntent = Intent(this, NotificationReceiver::class.java).setAction(AudioApp.ACTION_PLAY)
        val pausePending = PendingIntent.getBroadcast(this, 0, pauseIntent, PendingIntent.FLAG_UPDATE_CURRENT)
        val nextIntent = Intent(this, NotificationReceiver::class.java).setAction(AudioApp.ACTION_NEXT)
        val nextPending = PendingIntent.getBroadcast(this, 0, nextIntent, PendingIntent.FLAG_UPDATE_CURRENT)
        val track = mediaList[serviceIndex]
        val thumbInit = baseContext.getCoverTrack(track.id.toContentUri())
        val thumb = thumbInit ?: BitmapFactory.decodeResource(resources, R.drawable.ic_music_large)
        buildCacheApp.iconPausedCaller = playImg
        buildCacheApp.titleTextCaller = track.title
        buildCacheApp.artistTextCaller = track.artist
        NowPlayerFragment.listener?.updateNowPlay(playImg, track)
        val notification = NotificationCompat.Builder(this, AudioApp.AUDIO_CHANNEL)
            .setSmallIcon(R.drawable.ic_track)
            .setLargeIcon(thumb)
            .setContentTitle(track.title)
            .setContentText(track.artist)
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