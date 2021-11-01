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
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import com.mohammadkk.myaudioplayer.PlayerListActivity
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
            val albums = requireContext().getAllAlbum()
            compareAlbums(albums)
            val adapter = AlbumGridAdapter(requireActivity(), albums)
            adapter.setOnClickItemViewAlbum {
                Intent(context, PlayerListActivity::class.java).apply {
                    putExtra("album_id", albums[it].id)
                    startActivity(this)
                }
            }
            binding.albumsGridView.adapter = adapter
        }
        binding.albumsGridView.layoutManager = GridLayoutManager(context, if (isPortraitScreen()) 2 else 4)
    }
}