package com.mohammadkk.myaudioplayer

import androidx.appcompat.app.AppCompatActivity
import com.mohammadkk.myaudioplayer.service.CallBackService
import android.content.ServiceConnection
import com.mohammadkk.myaudioplayer.service.MediaService
import com.mohammadkk.myaudioplayer.model.Songs
import com.google.android.material.imageview.ShapeableImageView
import android.widget.TextView
import android.widget.SeekBar
import android.widget.ImageButton
import com.google.android.material.floatingactionbutton.FloatingActionButton
import android.os.Bundle
import android.content.Intent
import androidx.core.content.ContextCompat
import android.widget.SeekBar.OnSeekBarChangeListener
import com.mohammadkk.myaudioplayer.helper.MusicUtil
import android.os.Looper
import android.content.ComponentName
import android.os.IBinder
import com.mohammadkk.myaudioplayer.service.MediaService.BindService
import android.content.res.ColorStateList
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat
import android.graphics.drawable.AnimatedVectorDrawable
import android.net.Uri
import android.os.Handler
import android.view.View
import android.widget.ImageView
import androidx.appcompat.widget.Toolbar
import com.mohammadkk.myaudioplayer.helper.formatTimeMusic
import com.mohammadkk.myaudioplayer.helper.getAlbumCoverByUri
import java.lang.Exception
import java.lang.IllegalStateException
import java.util.ArrayList

class PlayerActivity : AppCompatActivity(), CallBackService, ServiceConnection {
    private var mediaService: MediaService? = null
    private var currentTime = 0
    private var totalTime = 0
    private var songsListPlayer = ArrayList<Songs>()
    private lateinit var actionTop: Toolbar
    private lateinit var coverMusic: ShapeableImageView
    private lateinit var titleMusic: TextView
    private lateinit var artistMusic: TextView
    private lateinit var durationPlayedMusic: TextView
    private lateinit var totalDurationMusic: TextView
    private lateinit var sliderMusic: SeekBar
    private lateinit var btnPrevMusic: ImageButton
    private lateinit var btnNextMusic: ImageButton
    private lateinit var fabPlayPause: FloatingActionButton
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)
        initViewById()
        if (MainActivity.isFadeActivity) {
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
        }
        musicIndex = intent.getIntExtra("positionStart", 0)
        val list: ArrayList<out Songs>? = intent.getParcelableArrayListExtra("songs_list")
        if (list != null) {
            songsListPlayer.addAll(list)
        } else songsListPlayer = MediaService.mediaList
        MediaService.mediaList = songsListPlayer
        actionTop.setNavigationOnClickListener {  onBackPressed() }
    }

    private fun initViewById() {
        actionTop = findViewById(R.id.actionTop)
        coverMusic = findViewById(R.id.coverMusic)
        titleMusic = findViewById(R.id.titleMusic)
        artistMusic = findViewById(R.id.artistMusic)
        sliderMusic = findViewById(R.id.sliderMusic)
        durationPlayedMusic = findViewById(R.id.durationPlayedMusic)
        totalDurationMusic = findViewById(R.id.totalDurationMusic)
        btnPrevMusic = findViewById(R.id.btnPrevMusic)
        fabPlayPause = findViewById(R.id.fabPlayPause)
        btnNextMusic = findViewById(R.id.btnNextMusic)
    }
    override fun onStart() {
        super.onStart()
        val intentService = Intent(this, MediaService::class.java)
        intentService.putExtra("index_service", musicIndex)
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
        sliderMusic.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    currentTime = progress
                    durationPlayedMusic.text = currentTime.formatTimeMusic()
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
        changePrevMusic()
        changeNextMusic()
        changePlayPauseMusic()
    }

    private fun playMusic(pos: Int) {
        try {
            mediaService!!.reset()
            mediaService!!.createMediaPlayer(pos)
            mediaService!!.prepare()
            mediaService!!.start()
            setDrawableAnimationPlayPause(true)
            metaData(pos)
            mediaService!!.showNotification(R.drawable.ic_pause)
            musicIndex = pos
        } catch (e: Exception) {
            e.printStackTrace()
        }
        setMusicProgress()
    }
    private fun setMusicProgress() {
        currentTime = mediaService!!.currentPosition
        totalTime = mediaService!!.duration
        totalDurationMusic.text = totalTime.formatTimeMusic()
        durationPlayedMusic.text = currentTime.formatTimeMusic()
        sliderMusic.max = totalTime
        val handle = Handler(Looper.getMainLooper())
        runOnUiThread(object : Runnable {
            override fun run() {
                try {
                    currentTime = mediaService!!.currentPosition
                    durationPlayedMusic.text = currentTime.formatTimeMusic()
                    sliderMusic.progress = currentTime
                    handle.postDelayed(this, 1000)
                } catch (ed: IllegalStateException) {
                    ed.printStackTrace()
                }
            }
        })
    }
    private fun changePrevMusic() {
        btnPrevMusic.setOnClickListener { setPrevMusic() }
    }
    private fun changeNextMusic() {
        btnNextMusic.setOnClickListener { setNextMusic() }
    }
    private fun changePlayPauseMusic() {
        fabPlayPause.setOnClickListener { setPlayPauseMusic() }
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
        if (musicIndex < songsListPlayer.size - 1) musicIndex++ else musicIndex = 0
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
        val servicePosition = mediaService!!.positionService
        metaData(servicePosition)

        fabPlayPause.setImageResource(if (mediaService!!.isPlaying) R.drawable.ic_pause else R.drawable.ic_play)
        setMusic()
        setMusicProgress()
    }

    override fun onServiceDisconnected(name: ComponentName) {
        mediaService = null
    }
    private fun metaData(pos: Int) {
        titleMusic.text = songsListPlayer[pos].title
        artistMusic.text = songsListPlayer[pos].artist
        val cover = getAlbumCoverByUri(Uri.parse(songsListPlayer[pos].albumArt))
        if (cover != null) {
            coverMusic.setImageBitmap(cover)
            coverMusic.scaleType = ImageView.ScaleType.CENTER_CROP
            coverMusic.imageTintList = null
        } else {
            coverMusic.setImageResource(R.drawable.ic_songs)
            coverMusic.scaleType = ImageView.ScaleType.FIT_CENTER
            coverMusic.imageTintList = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.pink_500))
        }
    }
    private fun setDrawableAnimationPlayPause(isPlaying: Boolean) {
        val playAnimCompat: AnimatedVectorDrawableCompat
        val playAnim: AnimatedVectorDrawable
        if (isPlaying) {
            fabPlayPause.setImageDrawable(
                ContextCompat.getDrawable(
                    this,
                    R.drawable.play_to_pause
                )
            )
            val drawable = fabPlayPause.drawable
            if (drawable is AnimatedVectorDrawableCompat) {
                playAnimCompat = drawable
                playAnimCompat.start()
            } else if (drawable is AnimatedVectorDrawable) {
                playAnim = drawable
                playAnim.start()
            }
        } else {
            fabPlayPause.setImageDrawable(
                ContextCompat.getDrawable(
                    this,
                    R.drawable.pause_to_play
                )
            )
            val drawable = fabPlayPause.drawable
            if (drawable is AnimatedVectorDrawableCompat) {
                playAnimCompat = drawable
                playAnimCompat.start()
            } else if (drawable is AnimatedVectorDrawable) {
                playAnim = drawable
                playAnim.start()
            }
        }
    }

    companion object {
        private var musicIndex = 0
    }
}