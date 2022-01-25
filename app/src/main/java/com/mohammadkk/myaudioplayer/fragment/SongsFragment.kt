package com.mohammadkk.myaudioplayer.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import com.mohammadkk.myaudioplayer.MainActivity.Companion.isFadeActivity
import com.mohammadkk.myaudioplayer.MainActivity.Companion.isRestartActivity
import com.mohammadkk.myaudioplayer.PlayerActivity
import com.mohammadkk.myaudioplayer.adapter.SongsListAdapter
import com.mohammadkk.myaudioplayer.databinding.FragmentSongsBinding
import com.mohammadkk.myaudioplayer.extension.getAllAlbum
import com.mohammadkk.myaudioplayer.extension.getAllSongs
import com.mohammadkk.myaudioplayer.model.Songs
import java.util.*


class SongsFragment : BaseFragment() {
    private lateinit var binding: FragmentSongsBinding
    private lateinit var adapter: SongsListAdapter
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentSongsBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        runTimeViewLoader()
        binding.songsListView.layoutManager = GridLayoutManager(requireContext(), if (isPortraitScreen()) 1 else 2)
    }

    override fun runTimeViewLoader() {
        val tempSongs = ArrayList<Songs>()
        requireContext().getAllAlbum().forEach {
            tempSongs.addAll(requireContext().getAllSongs(it.id))
        }
        compareSongs(tempSongs)
        adapter = SongsListAdapter(requireActivity(), tempSongs)
        adapter.setOnClickItemViewSong {
            Intent(requireContext(), PlayerActivity::class.java).apply {
                putExtra("positionStart", it)
                putExtra("songs_list", tempSongs)
                isFadeActivity = false
                isRestartActivity = true
                startActivity(this)
            }
        }
        binding.songsListView.adapter = adapter
    }
}