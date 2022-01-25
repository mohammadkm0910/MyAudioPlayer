package com.mohammadkk.myaudioplayer.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import com.mohammadkk.myaudioplayer.PlayerListActivity
import com.mohammadkk.myaudioplayer.adapter.AlbumGridAdapter
import com.mohammadkk.myaudioplayer.databinding.FragmentAlbumsBinding
import com.mohammadkk.myaudioplayer.extension.getAllAlbum

class AlbumsFragment : BaseFragment() {
    private lateinit var binding: FragmentAlbumsBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentAlbumsBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        runTimeViewLoader()
        val span = if (isPortraitScreen()) 2 else 4
        binding.albumsGridView.layoutManager = GridLayoutManager(context,span)
    }
    override fun runTimeViewLoader() {
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
}