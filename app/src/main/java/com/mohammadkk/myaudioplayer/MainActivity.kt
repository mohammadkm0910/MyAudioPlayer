package com.mohammadkk.myaudioplayer

import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.os.IBinder
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.annotation.ColorRes
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.ActionMode
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.mohammadkk.myaudioplayer.adapter.TracksAdapter
import com.mohammadkk.myaudioplayer.databinding.ActivityMainBinding
import com.mohammadkk.myaudioplayer.databinding.ToastBinding
import com.mohammadkk.myaudioplayer.extension.changeFragment
import com.mohammadkk.myaudioplayer.extension.deletePathStore
import com.mohammadkk.myaudioplayer.extension.hasPermission
import com.mohammadkk.myaudioplayer.extension.rescanPaths
import com.mohammadkk.myaudioplayer.fragment.AlbumsFragment
import com.mohammadkk.myaudioplayer.fragment.ArtistsFragment
import com.mohammadkk.myaudioplayer.fragment.NowPlayerFragment
import com.mohammadkk.myaudioplayer.fragment.SongsFragment
import com.mohammadkk.myaudioplayer.helper.Constants
import com.mohammadkk.myaudioplayer.service.MediaService
import com.mohammadkk.myaudioplayer.service.NotificationReceiver
import com.mohammadkk.myaudioplayer.service.ScanLibraryService


class MainActivity : AppCompatActivity(), ServiceConnection, UIController {
    private lateinit var binding: ActivityMainBinding
    private var actionMode: ActionMode? = null
    private var libraryService: ScanLibraryService? = null
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initView()
        if (savedInstanceState == null) {
            supportFragmentManager.changeFragment(SongsFragment(), Constants.TAG_SONGS_FRAGMENT)
            binding.bottomNavigationMusic.selectedItemId = R.id.navSongs
        }
    }
    private fun createToast(message: String, @ColorRes color: Int) {
        val toast = Toast(applicationContext)
        val toastBinding = ToastBinding.inflate(layoutInflater)
        toastBinding.root.background = GradientDrawable().apply {
            setColor(ContextCompat.getColor(this@MainActivity, color))
            cornerRadius = resources.getDimension(R.dimen.corner_rounded_toast)
        }
        toastBinding.tvToast.text = message
        @Suppress("DEPRECATION")
        toast.view = toastBinding.root
        toast.duration = Toast.LENGTH_SHORT
        toast.show()
    }
    private fun initView() {
        binding.bottomNavigationMusic.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.navSongs -> {
                    supportFragmentManager.changeFragment(SongsFragment(), Constants.TAG_SONGS_FRAGMENT)
                }
                R.id.navAlbums -> {
                    supportFragmentManager.changeFragment(AlbumsFragment(), Constants.TAG_ALBUMS_FRAGMENT)
                    actionMode?.finish()
                }
                R.id.navArtists -> {
                    supportFragmentManager.changeFragment(ArtistsFragment(), Constants.TAG_ARTISTS_FRAGMENT)
                    actionMode?.finish()
                }
            }
            true
        }
    }
    internal fun createActionMode(tracksAdapter: TracksAdapter) {
        if (actionMode == null) {
            actionMode = startSupportActionMode(tracksAdapter.createActionCallback())
        } else {
            if (tracksAdapter.getSelectedItemCount() == 0) {
                actionMode?.finish()
            }
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
    override fun onDownActionMode() {
        if (actionMode != null) {
            actionMode = null
        }
    }
    override fun onUpdateActionMode(title: String) {
        actionMode?.title = title
        actionMode?.invalidate()
    }
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.exitApp -> {
                if (MediaService.getIsExists()) {
                    Intent(this, NotificationReceiver::class.java).apply {
                        action = AudioApp.ACTION_STOP_SERVICE
                        sendBroadcast(this)
                    }
                }
                finish()
            }
            R.id.updateLibrary -> {
                createToast("Updating the library...", R.color.blue_A700)
                createToast("please wait...", R.color.blue_A400)
                libraryService?.scanRequireLibrary { library, filesNot ->
                    filesNot.forEach { deletePathStore(it.absolutePath) }
                    library.forEach { rescanPaths(arrayOf(it.absolutePath)) }
                    createToast("Refresh the page!!", R.color.green_A700)
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }
    override fun onStart() {
        super.onStart()
        val intentService = Intent(this, ScanLibraryService::class.java)
        startService(intentService)
        bindService(intentService, this, BIND_AUTO_CREATE)
        val nowPlayerFragment = supportFragmentManager.findFragmentById(R.id.nowPlayerFrag)
        if (nowPlayerFragment != null && nowPlayerFragment is NowPlayerFragment) {
            nowPlayerFragment.updateView()
        }
    }
    override fun onStop() {
        super.onStop()
        unbindService(this)
    }
    override fun onResume() {
        super.onResume()
        if (!hasPermission()) {
            startActivity(Intent(this, SplashActivity::class.java))
            finish()
        }
        binding.nowPlayerFrag.isVisible = MediaService.getIsExists()
    }
    override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
        libraryService = (service as ScanLibraryService.LocalService).service
    }
    override fun onServiceDisconnected(name: ComponentName?) {
        libraryService = null
    }
    companion object {
        @JvmField
        var isFadeActivity = false
        @JvmField
        var isRestartActivity = false
    }
}