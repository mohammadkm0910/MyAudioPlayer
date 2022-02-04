package com.mohammadkk.myaudioplayer

import android.Manifest
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.mohammadkk.myaudioplayer.databinding.ActivityMainBinding
import com.mohammadkk.myaudioplayer.extension.changeFragment
import com.mohammadkk.myaudioplayer.extension.hasPermission
import com.mohammadkk.myaudioplayer.fragment.*
import com.mohammadkk.myaudioplayer.helper.Constants
import com.mohammadkk.myaudioplayer.service.MediaService
import com.mohammadkk.myaudioplayer.service.NotificationReceiver

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var handlePermission: ActivityResultLauncher<String>? = null
    private lateinit var pref: SharedPreferences
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        pref = getSharedPreferences("cache_service", MODE_PRIVATE)
        initView()
        if (savedInstanceState == null) {
            supportFragmentManager.changeFragment(SongsFragment(), Constants.TAG_SONGS_FRAGMENT)
            binding.bottomNavigationMusic.selectedItemId = R.id.navSongs
        }
        handlePermission = registerForActivityResult(ActivityResultContracts.RequestPermission()) { permission->
            if (permission) {
                runView()
            } else {
                MaterialAlertDialogBuilder(this).setTitle(R.string.app_name)
                    .setMessage(R.string.permission_message)
                    .setCancelable(false)
                    .setPositiveButton(android.R.string.ok) { dialog, _ ->
                        val detail = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                        detail.apply {
                            addCategory(Intent.CATEGORY_DEFAULT)
                            addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
                            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            data = Uri.fromParts("package", packageName, null)
                        }
                        startActivity(detail)
                        isRuntimePermission = true
                        dialog.dismiss()
                    }
                    .setNegativeButton(android.R.string.cancel) { dialog, _ ->
                        Toast.makeText(applicationContext, getString(R.string.permission_deny), Toast.LENGTH_SHORT).show()
                        finish()
                        moveTaskToBack(true)
                        dialog.dismiss()
                    }.create().show()
            }
        }
        if (!hasPermission()) {
            handlePermission?.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
    }
    private fun runView() {
        if (getCurrentFragment() != null && getCurrentFragment() is BaseFragment) {
            (getCurrentFragment() as BaseFragment).runTimeViewLoader()
        }
    }
    private fun getCurrentFragment(): Fragment? {
        val fragManager = supportFragmentManager
        for (tag in Constants.TAGS_FRAGMENT) {
            val fragment = fragManager.findFragmentByTag(tag)
            if (fragment != null && fragment.isVisible) {
                return fragment
            }
        }
        return null
    }
    private fun initView() {
        binding.bottomNavigationMusic.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.navSongs -> {
                    supportFragmentManager.changeFragment(SongsFragment(), Constants.TAG_SONGS_FRAGMENT)
                }
                R.id.navAlbums -> {
                    supportFragmentManager.changeFragment(AlbumsFragment(), Constants.TAG_ALBUMS_FRAGMENT)
                }
                R.id.navArtists -> {
                    supportFragmentManager.changeFragment(ArtistsFragment(), Constants.TAG_ARTISTS_FRAGMENT)
                }
            }
            true
        }
    }
    override fun onBackPressed() {
        when (binding.bottomNavigationMusic.selectedItemId) {
            R.id.navArtists -> {
                supportFragmentManager.changeFragment(AlbumsFragment(), Constants.TAG_ALBUMS_FRAGMENT)
                binding.bottomNavigationMusic.selectedItemId = R.id.navAlbums
            }
            R.id.navAlbums -> {
                supportFragmentManager.changeFragment(SongsFragment(), Constants.TAG_SONGS_FRAGMENT)
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
            if (MediaService.getIsExists()) {
                Intent(this, NotificationReceiver::class.java).apply {
                    action = AudioApp.ACTION_STOP_SERVICE
                    sendBroadcast(this)
                }
            }
            finish()
        }
        return super.onOptionsItemSelected(item)
    }
    override fun onStart() {
        super.onStart()
        val nowPlayerFragment = supportFragmentManager.findFragmentById(R.id.nowPlayerFrag)
        if (nowPlayerFragment != null && nowPlayerFragment is NowPlayerFragment) {
            nowPlayerFragment.updateView()
        }
    }
    override fun onResume() {
        super.onResume()
        if (isRuntimePermission && hasPermission()) {
            runView()
            isRuntimePermission = false
        }
        binding.nowPlayerFrag.isVisible = MediaService.getIsExists()
    }
    companion object {
        private var isRuntimePermission = false
        @JvmField
        var isFadeActivity = false
        @JvmField
        var isRestartActivity = false
    }
}