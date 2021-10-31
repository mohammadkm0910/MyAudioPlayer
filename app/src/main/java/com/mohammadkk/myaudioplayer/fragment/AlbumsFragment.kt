package com.mohammadkk.myaudioplayer.fragment

import android.view.LayoutInflater
import android.view.ViewGroup
import android.os.Bundle
import com.mohammadkk.myaudioplayer.R
import androidx.recyclerview.widget.RecyclerView
import com.mohammadkk.myaudioplayer.adapter.AlbumGridAdapter
import com.mohammadkk.myaudioplayer.MainActivity
import android.content.Intent
import com.mohammadkk.myaudioplayer.PlayerActivity
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import com.mohammadkk.myaudioplayer.MainActivity.Companion.songsAllList
import com.mohammadkk.myaudioplayer.databinding.FragmentAlbumsBinding
import com.mohammadkk.myaudioplayer.helper.getAllAlbum
import com.mohammadkk.myaudioplayer.helper.getAllSongs
import com.mohammadkk.myaudioplayer.model.Songs
import java.util.ArrayList

class AlbumsFragment : RequireFragment() {
    private lateinit var binding: FragmentAlbumsBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentAlbumsBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        runTimePermission {
            val tempSongs = ArrayList<Songs>()
            requireContext().getAllAlbum().forEach {
                tempSongs.addAll(requireContext().getAllSongs(it.id))
            }
            songsAllList = tempSongs
            compareTo(tempSongs)
            val adapter = AlbumGridAdapter(requireActivity(), songsAllList)
            adapter.setOnClickItemViewAlbum {
                Intent(context, PlayerActivity::class.java).apply {
                    putExtra("positionStart", it)
                    putExtra("songs_list", tempSongs)
                    MainActivity.isFadeActivity = false
                    MainActivity.isRestartActivity = true
                    startActivity(this)
                }
            }
            binding.albumsGridView.adapter = adapter
        }
        binding.albumsGridView.layoutManager = GridLayoutManager(context, if (isPortraitScreen()) 2 else 4)
    }
}