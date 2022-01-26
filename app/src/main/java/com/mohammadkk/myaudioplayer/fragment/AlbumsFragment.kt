package com.mohammadkk.myaudioplayer.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mohammadkk.myaudioplayer.PlayerListActivity
import com.mohammadkk.myaudioplayer.adapter.AlbumsAdapter
import com.mohammadkk.myaudioplayer.databinding.FragmentAlbumsBinding
import com.mohammadkk.myaudioplayer.helper.Constants

class AlbumsFragment : BaseFragment() {
    private lateinit var binding: FragmentAlbumsBinding
    private lateinit var adapter: AlbumsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        adapter = AlbumsAdapter(requireContext())
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentAlbumsBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        runTimeViewLoader()
        initializeLayout(binding.albumsGridView, 2)
    }
    override fun runTimeViewLoader() {
        val albums = musicUtil.fetchAllAlbum()
        adapter.updateAlbumList(albums)
        adapter.setOnItemClickListener { position ->
           onItemClickForList(position)
        }
        binding.albumsGridView.adapter = adapter
    }
    override fun onItemClickForList(position: Int) {
        Intent(context, PlayerListActivity::class.java).apply {
            putExtra(Constants.EXTRA_PAGE_TYPE, "album")
            putExtra(Constants.EXTRA_PAGE_SELECTED_ID, adapter.getAlbum(position).id)
            startActivity(this)
        }
    }
}