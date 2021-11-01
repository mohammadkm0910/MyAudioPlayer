package com.mohammadkk.myaudioplayer.fragment

import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.mohammadkk.myaudioplayer.MainActivity
import com.mohammadkk.myaudioplayer.MainActivity.Companion.isFadeActivity
import com.mohammadkk.myaudioplayer.MainActivity.Companion.isRestartActivity
import com.mohammadkk.myaudioplayer.PlayerActivity
import com.mohammadkk.myaudioplayer.R
import com.mohammadkk.myaudioplayer.adapter.SongsListAdapter
import com.mohammadkk.myaudioplayer.databinding.FragmentSongsBinding
import com.mohammadkk.myaudioplayer.helper.getAllAlbum
import com.mohammadkk.myaudioplayer.helper.getAllSongs
import com.mohammadkk.myaudioplayer.model.Songs
import java.util.ArrayList


class SongsFragment : RequireFragment() {
    private lateinit var binding: FragmentSongsBinding
    private lateinit var adapter: SongsListAdapter
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentSongsBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        runTimePermission {
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
        binding.songsListView.layoutManager = GridLayoutManager(requireContext(), if (isPortraitScreen()) 1 else 2)
    }
}