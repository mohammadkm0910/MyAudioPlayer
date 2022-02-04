package com.mohammadkk.myaudioplayer

import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.content.res.ColorStateList
import android.graphics.drawable.AnimatedVectorDrawable
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import androidx.annotation.DrawableRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat
import com.mohammadkk.myaudioplayer.databinding.ActivityPlayerBinding
import com.mohammadkk.myaudioplayer.extension.albumIdToArt
import com.mohammadkk.myaudioplayer.extension.formatTimeMusic
import com.mohammadkk.myaudioplayer.model.Track
import com.mohammadkk.myaudioplayer.service.MediaService
import com.mohammadkk.myaudioplayer.service.MediaService.BindService
import com.mohammadkk.myaudioplayer.service.ServiceListener

class PlayerActivity : AppCompatActivity(), ServiceListener, ServiceConnection {
    private lateinit var binding: ActivityPlayerBinding
    private var mediaService: MediaService? = null
    private var currentIndex = 0
    private var currentTime = 0
    private var totalTime = 0
    private var tracksPlayer = ArrayList<Track>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.actionTop)
        supportActionBar?.title = ""
        if (MainActivity.isFadeActivity) {
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
        }
        buildCacheApp.getStoreTracks().run {
            tracksPlayer = this ?: MediaService.getMediaList()
        }
        currentIndex = buildCacheApp.globalTrackIndexCaller
        if (currentIndex != -1 && tracksPlayer.isEmpty())
            onBackPressed()
        binding.actionTop.setNavigationOnClickListener {  onBackPressed() }
    }
    override fun onStart() {
        super.onStart()
        val intentService = Intent(this, MediaService::class.java)
        ContextCompat.startForegroundService(this, intentService)
        bindService(intentService, this, BIND_AUTO_CREATE)
    }
    override fun onPause() {
        super.onPause()
        if (MainActivity.isFadeActivity) {
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
        }
    }
    override fun onStop() {
        super.onStop()
        unbindService(this)
    }
    private fun setMusic() {
        binding.trackSlider.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    currentTime = progress
                    binding.tvPlayedTimeTrack.text = currentTime.formatTimeMusic()
                }
            }
            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {
                currentTime = seekBar.progress
                mediaService!!.seekTo(currentTime)
            }
        })
        if (MainActivity.isRestartActivity) {
            playMusic(currentIndex)
            MainActivity.isRestartActivity = false
        }
        setCountTitle()
        binding.btnPreviousTrack.setOnClickListener { onPreviousTrack() }
        binding.btnNextTrack.setOnClickListener { onNextTrack() }
        binding.fabPlayPause.setOnClickListener { onPlayPauseTrack() }
    }
    private fun playMusic(pos: Int) {
        try {
            mediaService!!.reset()
            mediaService!!.createMediaPlayer(pos)
            mediaService!!.setDefaultAudioStream()
            mediaService!!.prepareAsync()
            setMetaData()
            setCountTitle()
            setImageAnimatedVector(R.drawable.play_to_pause)
            mediaService!!.showNotification(R.drawable.ic_pause)
            currentIndex = pos
        } catch (e: Exception) {
            e.printStackTrace()
        }
        setMusicProgress()
    }
    private fun setCountTitle() {
        binding.tvCountTrack.text = String.format("%s/%s", currentIndex + 1, tracksPlayer.size)
    }
    private fun setMusicProgress() {
        currentTime = mediaService!!.currentPosition
        totalTime = mediaService!!.duration
        binding.tvTotalTimeTrack.text = totalTime.formatTimeMusic()
        binding.tvPlayedTimeTrack.text = currentTime.formatTimeMusic()
        binding.trackSlider.max = totalTime
        val handle = Handler(Looper.getMainLooper())
        runOnUiThread(object : Runnable {
            override fun run() {
                try {
                    currentTime = mediaService!!.currentPosition
                    binding.tvPlayedTimeTrack.text = currentTime.formatTimeMusic()
                    binding.trackSlider.progress = currentTime
                    handle.postDelayed(this, 1000)
                } catch (ed: IllegalStateException) {
                    ed.printStackTrace()
                }
            }
        })
    }
    override fun onPreviousTrack() {
        if (currentIndex > 0) {
            currentIndex--
        } else {
            currentIndex = tracksPlayer.size - 1
        }
        playMusic(currentIndex)
    }
    override fun onPlayPauseTrack() {
        if (mediaService!!.isPlaying) {
            mediaService!!.pause()
            mediaService!!.showNotification(R.drawable.ic_play)
            setImageAnimatedVector(R.drawable.pause_to_play)
        } else {
            mediaService!!.start()
            mediaService!!.showNotification(R.drawable.ic_pause)
            setImageAnimatedVector(R.drawable.play_to_pause)
        }
    }
    override fun onNextTrack() {
        if (currentIndex < tracksPlayer.size - 1) {
            currentIndex++
        } else currentIndex = 0
        playMusic(currentIndex)
    }
    override fun onServiceConnected(name: ComponentName, service: IBinder) {
        mediaService = (service as BindService).service
        mediaService!!.setOnListenerService(this)
        setMetaData()
        if (mediaService!!.isPlaying) {
            setImageAnimatedVector(R.drawable.play_to_pause)
        } else {
            setImageAnimatedVector(R.drawable.pause_to_play)
        }
        setMusic()
        setMusicProgress()
    }
    override fun onServiceDisconnected(name: ComponentName) {
        mediaService = null
    }
    private fun setMetaData() {
        val track = tracksPlayer[currentIndex]
        binding.tvTitleTrack.text = track.title
        binding.tvAlbumTrack.text = track.album
        binding.tvArtistTrack.text = track.artist
        track.albumId.albumIdToArt(this) { art ->
            if (art != null) {
                binding.trackImage.setImageBitmap(art)
                binding.trackImage.scaleType = ImageView.ScaleType.CENTER_CROP
                binding.trackImage.imageTintList = null
            } else {
                binding.trackImage.setImageResource(R.drawable.ic_track)
                binding.trackImage.scaleType = ImageView.ScaleType.FIT_CENTER
                binding.trackImage.imageTintList = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.pink_500))
            }
        }
    }
    private fun setImageAnimatedVector(@DrawableRes id: Int) {
        binding.fabPlayPause.setImageDrawable(ContextCompat.getDrawable(this, id))
        val drawable = binding.fabPlayPause.drawable
        if (drawable is AnimatedVectorDrawableCompat) {
            drawable.start()
        } else if (drawable is AnimatedVectorDrawable) {
            drawable.start()
        }
    }
}