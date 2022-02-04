package com.mohammadkk.myaudioplayer

import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.content.res.ColorStateList
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.mohammadkk.myaudioplayer.databinding.ActivityPlayerBinding
import com.mohammadkk.myaudioplayer.extension.albumIdToArt
import com.mohammadkk.myaudioplayer.extension.formatTimeMusic
import com.mohammadkk.myaudioplayer.extension.getResDrawable
import com.mohammadkk.myaudioplayer.extension.setResVectorDrawable
import com.mohammadkk.myaudioplayer.model.Track
import com.mohammadkk.myaudioplayer.service.CallBackService
import com.mohammadkk.myaudioplayer.service.MediaService
import com.mohammadkk.myaudioplayer.service.MediaService.BindService

class PlayerActivity : AppCompatActivity(), CallBackService, ServiceConnection {
    private lateinit var binding: ActivityPlayerBinding
    private var mediaService: MediaService? = null
    private var musicIndex = 0
    private var currentTime = 0
    private var totalTime = 0
    private val songsListPlayer = ArrayList<Track>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.actionTop)
        supportActionBar?.title = ""
        if (MainActivity.isFadeActivity) {
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
        }
        musicIndex = buildCacheApp.globalTrackIndexCaller
        val list: ArrayList<out Track>? = buildCacheApp.getStoreTracks()
        songsListPlayer.clear()
        if (list != null) {
            songsListPlayer.addAll(list)
        } else songsListPlayer.addAll(MediaService.getMediaList())
        if (musicIndex != -1 && songsListPlayer.isEmpty())
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
            playMusic(musicIndex)
            MainActivity.isRestartActivity = false
        }
        setCountTitle()
        binding.btnPreviousTrack.setOnClickListener { setPrevMusic() }
        binding.btnNextTrack.setOnClickListener { setNextMusic() }
        binding.fabPlayPause.setOnClickListener { setPlayPauseMusic() }
    }
    private fun playMusic(pos: Int) {
        try {
            mediaService!!.reset()
            mediaService!!.createMediaPlayer(pos)
            mediaService!!.prepare()
            mediaService!!.start()
            setCountTitle()
            setDrawableAnimationPlayPause(true)
            metaData(pos)
            mediaService!!.showNotification(R.drawable.ic_pause)
            musicIndex = pos
        } catch (e: Exception) {
            e.printStackTrace()
        }
        setMusicProgress()
    }
    private fun setCountTitle() {
        binding.tvCountTrack.text = String.format("%s/%s", musicIndex + 1, songsListPlayer.size)
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
    override fun setPrevMusic() {
        if (musicIndex > 0) {
            musicIndex--
        } else {
            musicIndex = songsListPlayer.size - 1
        }
        playMusic(musicIndex)
    }
    override fun setNextMusic() {
        if (musicIndex < songsListPlayer.size - 1) {
            musicIndex++
        } else musicIndex = 0
        playMusic(musicIndex)
    }
    override fun setPlayPauseMusic() {
        if (mediaService!!.isPlaying) {
            mediaService!!.pause()
            mediaService!!.showNotification(R.drawable.ic_play)
            setDrawableAnimationPlayPause(false)
        } else {
            mediaService!!.start()
            mediaService!!.showNotification(R.drawable.ic_pause)
            setDrawableAnimationPlayPause(true)
        }
    }
    override fun onServiceConnected(name: ComponentName, service: IBinder) {
        mediaService = (service as BindService).service
        mediaService!!.setCallBackService(this)
        metaData(musicIndex)
        binding.fabPlayPause.setImageResource(if (mediaService!!.isPlaying) R.drawable.ic_pause else R.drawable.ic_play)
        setMusic()
        setMusicProgress()
    }

    override fun onServiceDisconnected(name: ComponentName) {
        mediaService = null
    }
    private fun metaData(pos: Int) {
        binding.tvTitleTrack.text = songsListPlayer[pos].title
        binding.tvAlbumTrack.text = songsListPlayer[pos].album
        binding.tvArtistTrack.text = songsListPlayer[pos].artist
        songsListPlayer[pos].albumId.albumIdToArt(this) { art ->
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
    private fun setDrawableAnimationPlayPause(isPlaying: Boolean) {
        if (isPlaying) {
            binding.fabPlayPause.setResVectorDrawable(getResDrawable(R.drawable.play_to_pause))
        } else {
            binding.fabPlayPause.setResVectorDrawable(getResDrawable(R.drawable.pause_to_play))
        }
    }
}