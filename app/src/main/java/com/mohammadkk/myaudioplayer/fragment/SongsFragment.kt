package com.mohammadkk.myaudioplayer.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mohammadkk.myaudioplayer.MainActivity.Companion.isFadeActivity
import com.mohammadkk.myaudioplayer.MainActivity.Companion.isRestartActivity
import com.mohammadkk.myaudioplayer.PlayerActivity
import com.mohammadkk.myaudioplayer.adapter.TracksAdapter
import com.mohammadkk.myaudioplayer.buildCacheApp
import com.mohammadkk.myaudioplayer.databinding.FragmentSongsBinding


class SongsFragment : BaseFragment() {
    private lateinit var binding: FragmentSongsBinding
    private lateinit var adapter: TracksAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentSongsBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        adapter = TracksAdapter(requireContext())
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeLayout(binding.songsListView)
        binding.songsListView.adapter = adapter
        runTimeViewLoader()
    }
    override fun runTimeViewLoader() {
        val tracks = musicUtil.fetchAllTracks()
        adapter.updateTrackList(tracks)
        adapter.setOnItemClickListener { position ->
            onItemClickForList(position)
        }
    }
    override fun onItemClickForList(position: Int) {
        Intent(requireContext(), PlayerActivity::class.java).apply {
            buildCacheApp.globalTrackIndexCaller = position
            buildCacheApp.storeTracks(adapter.getTracks())
            isFadeActivity = false
            isRestartActivity = true
            startActivity(this)
        }
    }
}