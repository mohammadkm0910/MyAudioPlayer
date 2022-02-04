package com.mohammadkk.myaudioplayer.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.mohammadkk.myaudioplayer.PlayerListActivity
import com.mohammadkk.myaudioplayer.adapter.AlbumsAdapter
import com.mohammadkk.myaudioplayer.databinding.FragmentAlbumsBinding
import com.mohammadkk.myaudioplayer.helper.Constants
import com.mohammadkk.myaudioplayer.viewmodel.AlbumViewModel

class AlbumsFragment : BaseFragment() {
    private lateinit var binding: FragmentAlbumsBinding
    private var albumViewModel: AlbumViewModel? = null
    private lateinit var adapter: AlbumsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        adapter = AlbumsAdapter(requireContext())
        albumViewModel = ViewModelProvider(requireActivity())[AlbumViewModel::class.java]
        rescanDevice()
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentAlbumsBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeLayout(binding.albumsGridView, 2)
        initializeListAdapter()
    }
    override fun rescanDevice() {
        albumViewModel?.scanAlbumInDevice()
    }
    override fun initializeListAdapter() {
        binding.albumsGridView.adapter = adapter
        albumViewModel!!.deviceAlbum.observe(viewLifecycleOwner) { albums ->
            albums?.run {
                adapter.addAll(albums)
                adapter.refresh()
            }
        }
        adapter.setOnItemClickListener { position ->
            onItemClickForList(position)
        }
    }
    override fun onItemClickForList(position: Int) {
        Intent(context, PlayerListActivity::class.java).apply {
            putExtra(Constants.EXTRA_PAGE_TYPE, "album")
            putExtra(Constants.EXTRA_PAGE_SELECTED_ID, adapter.getAlbum(position).id)
            startActivity(this)
        }
    }
}