package com.mohammadkk.myaudioplayer

import androidx.appcompat.app.AppCompatActivity
import android.content.SharedPreferences
import android.os.Bundle
import com.mohammadkk.myaudioplayer.model.Songs
import com.mohammadkk.myaudioplayer.service.MediaService
import android.content.Intent
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.mohammadkk.myaudioplayer.fragment.SongsFragment
import com.mohammadkk.myaudioplayer.fragment.AlbumsFragment
import com.mohammadkk.myaudioplayer.fragment.ArtistsFragment
import com.mohammadkk.myaudioplayer.databinding.ActivityMainBinding
import com.mohammadkk.myaudioplayer.fragment.RequireFragment
import com.mohammadkk.myaudioplayer.viewmodel.SharedPreferenceIntLiveData
import java.util.ArrayList

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var pref: SharedPreferences
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        pref = getSharedPreferences("cache_service", MODE_PRIVATE)
        initView()
        if (savedInstanceState == null) {
            navigate(SongsFragment())
            binding.bottomNavigationMusic.selectedItemId = R.id.navSongs
        }
        binding.miniTitleMusic.isSelected = true
        binding.miniMusic.setOnClickListener {
            if (MediaService.isService) {
                val playIntent = Intent(this@MainActivity, PlayerActivity::class.java)
                playIntent.putExtra("positionStart", pref.getInt("play_pos", 0))
                isRestartActivity = false
                isFadeActivity = true
                startActivity(playIntent)
            }
        }
        binding.miniBtnPlayPause.setOnClickListener {
            if (MediaService.isService) {
                Intent(this, NotificationReceiver::class.java).apply {
                    action = MediaApplication.ACTION_PLAY
                    sendBroadcast(this)
                }
            }
        }
    }

    private fun initView() {
        binding.bottomNavigationMusic.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.navSongs -> {
                    if (binding.bottomNavigationMusic.selectedItemId != R.id.navSongs) {
                        navigate(SongsFragment())
                    }
                }
                R.id.navAlbums ->{
                    if (binding.bottomNavigationMusic.selectedItemId != R.id.navAlbums) {
                        navigate(AlbumsFragment())
                    }
                }
                R.id.navArtists -> {
                    if (binding.bottomNavigationMusic.selectedItemId != R.id.navArtists) {
                        navigate(ArtistsFragment())
                    }
                }
            }
            true
        }
    }
    private fun navigate(fragment: RequireFragment) {
        val ft = supportFragmentManager.beginTransaction()
        ft.replace(R.id.fragmentContainer, fragment).commit()
    }
    override fun onBackPressed() {
        when (binding.bottomNavigationMusic.selectedItemId) {
            R.id.navArtists -> {
                navigate(AlbumsFragment())
                binding.bottomNavigationMusic.selectedItemId = R.id.navAlbums
            }
            R.id.navAlbums -> {
                navigate(SongsFragment())
                binding.bottomNavigationMusic.selectedItemId = R.id.navSongs
            }
            else -> super.onBackPressed()
        }
    }
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.exitApp) {
            if (MediaService.isService) {
                Intent(this, NotificationReceiver::class.java).apply {
                    action = MediaApplication.ACTION_STOP_SERVICE
                    sendBroadcast(this)
                }
            }
            finish()
        }
        return super.onOptionsItemSelected(item)
    }
    override fun onResume() {
        super.onResume()
        SharedPreferenceIntLiveData(pref, "play_state", R.drawable.ic_pause).observe(this, {
            binding.miniBtnPlayPause.setImageResource(it)
        })
        SharedPreferenceIntLiveData(pref, "play_pos", 0).observe(this, {
            if (MediaService.mediaList.isNotEmpty()) {
                val title = MediaService.mediaList[it].title
                val artist = MediaService.mediaList[it].artist
                binding.miniTitleMusic.text = String.format("%s - %s", title, artist)
            }
        })
        if (MediaService.isService) {
            binding.miniMusic.visibility = View.VISIBLE
        } else {
            binding.miniMusic.visibility = View.GONE
        }
    }

    companion object {
        @JvmField
        var isFadeActivity = false
        @JvmField
        var isRestartActivity = false
    }
}