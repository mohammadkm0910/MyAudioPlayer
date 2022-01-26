package com.mohammadkk.myaudioplayer.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mohammadkk.myaudioplayer.PlayerListActivity
import com.mohammadkk.myaudioplayer.adapter.ArtistsAdapter
import com.mohammadkk.myaudioplayer.databinding.FragmentArtistsBinding
import com.mohammadkk.myaudioplayer.helper.Constants

class ArtistsFragment : BaseFragment() {
    private lateinit var binding: FragmentArtistsBinding
    private lateinit var adapter: ArtistsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        adapter = ArtistsAdapter(requireContext())
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentArtistsBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        runTimeViewLoader()
        initializeLayout(binding.artistsGridView, 2)
    }
    override fun runTimeViewLoader() {
        val artists = musicUtil.fetchAllArtist()
        compareArtists(artists)
        adapter.updateArtistList(artists)
        adapter.setOnItemClickListener { position ->
            onItemClickForList(position)
        }
        binding.artistsGridView.adapter = adapter
    }
    override fun onItemClickForList(position: Int) {
        Intent(context, PlayerListActivity::class.java).apply {
            putExtra(Constants.EXTRA_PAGE_TYPE, "artist")
            putExtra(Constants.EXTRA_PAGE_SELECTED_ID, adapter.getArtist(position).id)
            startActivity(this)
        }
    }
}