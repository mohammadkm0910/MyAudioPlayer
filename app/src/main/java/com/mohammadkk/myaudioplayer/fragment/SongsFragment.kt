package com.mohammadkk.myaudioplayer.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.mohammadkk.myaudioplayer.MainActivity.Companion.isFadeActivity
import com.mohammadkk.myaudioplayer.MainActivity.Companion.isRestartActivity
import com.mohammadkk.myaudioplayer.PlayerActivity
import com.mohammadkk.myaudioplayer.adapter.TracksAdapter
import com.mohammadkk.myaudioplayer.buildCacheApp
import com.mohammadkk.myaudioplayer.databinding.FragmentSongsBinding
import com.mohammadkk.myaudioplayer.viewmodel.TrackViewModel


class SongsFragment : BaseFragment() {
    private lateinit var binding: FragmentSongsBinding
    private var trackViewModel: TrackViewModel? = null
    private lateinit var adapter: TracksAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        adapter = TracksAdapter(requireContext())
        trackViewModel = ViewModelProvider(requireActivity())[TrackViewModel::class.java]
        rescanDevice()
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentSongsBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeLayout(binding.songsListView)
        initializeListAdapter()
    }
    override fun rescanDevice() {
        trackViewModel?.scanTrackInDevice()
    }
    override fun initializeListAdapter() {
        binding.songsListView.adapter = adapter
        trackViewModel!!.deviceTrack.observe(viewLifecycleOwner) { tracks ->
            tracks?.run {
                adapter.addAll(this)
                adapter.refresh()
            }
        }
        adapter.setOnItemClickListener { position ->
            onItemClickForList(position)
        }
    }
    override fun onItemClickForList(position: Int) {
        Intent(requireContext(), PlayerActivity::class.java).apply {
            buildCacheApp.globalTrackIndexCaller = position
            buildCacheApp.storeTracks(adapter.getAllTracks())
            isFadeActivity = false
            isRestartActivity = true
            startActivity(this)
        }
    }
}