package com.mohammadkk.myaudioplayer.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.mohammadkk.myaudioplayer.PlayerListActivity
import com.mohammadkk.myaudioplayer.adapter.ArtistsAdapter
import com.mohammadkk.myaudioplayer.databinding.FragmentArtistsBinding
import com.mohammadkk.myaudioplayer.helper.Constants
import com.mohammadkk.myaudioplayer.viewmodel.ArtistViewModel

class ArtistsFragment : BaseFragment() {
    private lateinit var binding: FragmentArtistsBinding
    private var artistViewModel: ArtistViewModel? = null
    private lateinit var adapter: ArtistsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        adapter = ArtistsAdapter(requireContext())
        artistViewModel = ViewModelProvider(requireActivity())[ArtistViewModel::class.java]
        rescanDevice()
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentArtistsBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeLayout(binding.artistsGridView, 2)
        initializeListAdapter()
    }
    override fun rescanDevice() {
        artistViewModel?.scanArtistInDevice()
    }
    override fun initializeListAdapter() {
        binding.artistsGridView.adapter = adapter
        artistViewModel!!.deviceArtist.observe(viewLifecycleOwner) { artists ->
            artists?.run {
                adapter.addAll(artists)
                adapter.refresh()
            }
        }
        adapter.setOnItemClickListener { position ->
            onItemClickForList(position)
        }
    }
    override fun onItemClickForList(position: Int) {
        Intent(context, PlayerListActivity::class.java).apply {
            putExtra(Constants.EXTRA_PAGE_TYPE, "artist")
            putExtra(Constants.EXTRA_PAGE_SELECTED_ID, adapter.getArtist(position).id)
            startActivity(this)
        }
    }
}