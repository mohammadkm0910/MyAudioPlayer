package com.mohammadkk.myaudioplayer

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.mohammadkk.myaudioplayer.adapter.TracksAdapter
import com.mohammadkk.myaudioplayer.databinding.ActivityPlayerListBinding
import com.mohammadkk.myaudioplayer.extension.isPortraitScreen
import com.mohammadkk.myaudioplayer.helper.Constants
import com.mohammadkk.myaudioplayer.service.MediaService
import com.mohammadkk.myaudioplayer.viewmodel.TrackViewModel

class PlayerListActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPlayerListBinding
    private lateinit var tracksAdapter: TracksAdapter
    private var trackViewModel: TrackViewModel? = null
    private val typedAdapter: String get() = intent.getStringExtra(Constants.EXTRA_PAGE_TYPE) ?: "album"
    private val selectedId: Long get() = intent.getLongExtra(Constants.EXTRA_PAGE_SELECTED_ID, 0L)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayerListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
        tracksAdapter = TracksAdapter(this)
        trackViewModel = ViewModelProvider(this)[TrackViewModel::class.java]
        if (typedAdapter == "album") {
            trackViewModel?.scanTrackInDeviceByAlbum(selectedId)
        } else if (typedAdapter == "artist") {
            trackViewModel?.scanTrackInDeviceByArtist(selectedId)
        }
        setSupportActionBar(binding.toolbar)
        binding.toolbar.setNavigationOnClickListener { finish() }
        initializeListView()
        initializeListAdapter()
    }
    private fun initializeListView() {
        val count = if (isPortraitScreen) 1 else 2
        binding.tracksRV.setHasFixedSize(true)
        binding.tracksRV.layoutManager = GridLayoutManager(this, count)
        binding.tracksRV.adapter = tracksAdapter
    }
    private fun initializeListAdapter() {
        trackViewModel?.deviceTrack?.observe(this) { tracks->
            tracks?.run {
                tracksAdapter.addAll(this)
                tracksAdapter.refresh()
            }
        }
        tracksAdapter.setOnItemClickListener { position ->
            onItemClickAdapter(position)
        }
    }
    private fun onItemClickAdapter(position: Int) {
        buildCacheApp.globalTrackIndexCaller = position
        buildCacheApp.storeTracks(tracksAdapter.getAllTracks())
        val playerIntent = Intent(this, PlayerActivity::class.java)
        MainActivity.isRestartActivity = true
        startActivity(playerIntent)
    }
    override fun onResume() {
        super.onResume()
        binding.nowPlayerFrag.isVisible = MediaService.getIsExists()
    }
    override fun onPause() {
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
        super.onPause()
    }
    override fun onDestroy() {
        super.onDestroy()
        trackViewModel?.cancel()
    }
}