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
import android.util.Log
import androidx.core.app.NotificationCompat
import com.mohammadkk.myaudioplayer.AudioApp
import com.mohammadkk.myaudioplayer.PlayerActivity
import com.mohammadkk.myaudioplayer.R
import com.mohammadkk.myaudioplayer.buildCacheApp
import com.mohammadkk.myaudioplayer.extension.getCoverTrack
import com.mohammadkk.myaudioplayer.extension.toContentUri
import com.mohammadkk.myaudioplayer.fragment.NowPlayerFragment
import com.mohammadkk.myaudioplayer.helper.BuildUtil
import com.mohammadkk.myaudioplayer.helper.Constants
import com.mohammadkk.myaudioplayer.model.Track
import java.io.IOException


class MediaService : Service(), MediaPlayer.OnCompletionListener, MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener {
    private var bindService: IBinder = BindService()
    private var mediaPlayer: MediaPlayer? = null
    private var mMediaSession: MediaSessionCompat? = null
    private var listener: ServiceListener? = null
    private var serviceIndex = 0

    override fun onBind(intent: Intent): IBinder {
        return bindService
    }
    override fun onCreate() {
        super.onCreate()
        mediaPlayer = MediaPlayer()
        mMediaSession = MediaSessionCompat(baseContext, "MediaService")
        isExists = true
    }
    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mMediaSession?.isActive = false
        isExists = false
        buildCacheApp.requirClear()
    }
    fun setOnListenerService(listener: ServiceListener) {
        this.listener = listener
    }
    @Throws(IOException::class)
    fun createMediaPlayer(position: Int) {
        if (position >= 0 && position < trackList.size) {
            serviceIndex = position
            buildCacheApp.globalTrackIndexCaller = serviceIndex
            val track = trackList[position]
            mediaPlayer?.setDataSource(baseContext, track.id.toContentUri())
            mMediaSession?.isActive = true
            val metadata = MediaMetadataCompat.Builder()
                .putString(MediaMetadataCompat.METADATA_KEY_ALBUM, track.album)
                .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, track.artist)
                .putString(MediaMetadataCompat.METADATA_KEY_TITLE, track.title)
                .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, track.id.toString())
                .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, track.duration.toLong())
                .build()
            mMediaSession?.setMetadata(metadata)
        }
    }
    override fun onCompletion(mp: MediaPlayer?) {
        listener?.onNextTrack()
    }
    override fun onPrepared(mp: MediaPlayer?) {
        if (mediaPlayer?.isPlaying == false)
            mediaPlayer?.start()
    }
    override fun onError(mp: MediaPlayer?, what: Int, extra: Int): Boolean {
        val errorList = intArrayOf(
            MediaPlayer.MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK,
            MediaPlayer.MEDIA_ERROR_UNSUPPORTED,
            MediaPlayer.MEDIA_ERROR_UNKNOWN,
            MediaPlayer.MEDIA_ERROR_SERVER_DIED,
            MediaPlayer.MEDIA_ERROR_IO
        )
        for (error in errorList) {
            if (what == error) {
                Log.i("error_media_player", "error code: $error, extra: $extra")
            }
        }
        return false
    }
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        serviceIndex = buildCacheApp.globalTrackIndexCaller
        val tracks = buildCacheApp.getStoreTracks()
        if (tracks != null) trackList = tracks
        if (serviceIndex == -1 && trackList.isEmpty()) {
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
        mediaPlayer?.setOnErrorListener(this)
        return START_STICKY
    }
    fun reset() {
        mediaPlayer?.reset()
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
        mediaPlayer?.start()
    }
    fun seekTo(mSec: Int) {
        mediaPlayer?.seekTo(mSec)
    }
    fun pause() {
        mediaPlayer?.pause()
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
        val flags = Constants.PENDING_INTENT_FLAG
        val mi = Intent(this, PlayerActivity::class.java)
        val contentIntent = PendingIntent.getActivity(this, 0, mi, flags)
        val prevIntent = Intent(this, NotificationReceiver::class.java).setAction(AudioApp.ACTION_PREVIOUS)
        val prevPending = PendingIntent.getBroadcast(this, 0, prevIntent, flags)
        val pauseIntent = Intent(this, NotificationReceiver::class.java).setAction(AudioApp.ACTION_PLAY)
        val pausePending = PendingIntent.getBroadcast(this, 0, pauseIntent, flags)
        val nextIntent = Intent(this, NotificationReceiver::class.java).setAction(AudioApp.ACTION_NEXT)
        val nextPending = PendingIntent.getBroadcast(this, 0, nextIntent, flags)
        val track = trackList[serviceIndex]
        var cover = getCoverTrack(track.id.toContentUri())
        cover = cover ?: BitmapFactory.decodeResource(resources, R.drawable.ic_music_large)
        buildCacheApp.iconPausedCaller = playImg
        buildCacheApp.titleTextCaller = track.title
        buildCacheApp.artistTextCaller = track.artist
        NowPlayerFragment.listener?.updateNowPlay(playImg, track)
        val notification = NotificationCompat.Builder(this, AudioApp.AUDIO_CHANNEL)
            .setSmallIcon(R.drawable.ic_track)
            .setLargeIcon(cover)
            .setContentTitle(track.title)
            .setContentText(track.artist)
            .setContentIntent(contentIntent)
            .addAction(R.drawable.ic_skip_previous, "Previous", prevPending)
            .addAction(playImg, "Pause", pausePending)
            .addAction(R.drawable.ic_skip_next, "Next", nextPending)
            .setStyle(
                androidx.media.app.NotificationCompat.MediaStyle()
                    .setShowActionsInCompactView(0, 1, 2)
                    .setMediaSession(mMediaSession?.sessionToken)
            )
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setOnlyAlertOnce(true)
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
        private var trackList = ArrayList<Track>()
        fun getMediaList(): ArrayList<Track> = trackList
    }
}