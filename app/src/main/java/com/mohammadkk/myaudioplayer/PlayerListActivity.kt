package com.mohammadkk.myaudioplayer

import android.content.Intent
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import com.mohammadkk.myaudioplayer.adapter.SongsListAdapter
import com.mohammadkk.myaudioplayer.databinding.ActivityPlayerListBinding
import com.mohammadkk.myaudioplayer.helper.getAllSongs

class PlayerListActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPlayerListBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayerListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
        setSupportActionBar(binding.toolbar)
        binding.toolbar.setNavigationOnClickListener { finish() }
        displaySongs()
    }
    private fun displaySongs() {
        val albumId = intent.getLongExtra("album_id", 0)
        val songs = getAllSongs(albumId)
        songs.sortWith { o1, o2 -> o1.title.compareTo(o2.title, true) }
        val songsAdapter = SongsListAdapter(this, songs)
        val span = if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) 1 else 2
        binding.songsByAlbum.adapter = songsAdapter
        songsAdapter.setOnClickItemViewSong {
            Intent(this, PlayerActivity::class.java).apply {
                putExtra("positionStart", it)
                putExtra("songs_list", songs)
                MainActivity.isFadeActivity = false
                MainActivity.isRestartActivity = true
                startActivity(this)
            }
        }
        binding.songsByAlbum.layoutManager = GridLayoutManager(this, span)
    }
    override fun onPause() {
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
        super.onPause()
    }
}