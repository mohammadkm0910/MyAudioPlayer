package com.mohammadkk.myaudioplayer.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import com.mohammadkk.myaudioplayer.MainActivity
import com.mohammadkk.myaudioplayer.PlayerActivity
import com.mohammadkk.myaudioplayer.adapter.ArtistGridAdapter
import com.mohammadkk.myaudioplayer.databinding.FragmentArtistsBinding
import com.mohammadkk.myaudioplayer.extension.getAllAlbum
import com.mohammadkk.myaudioplayer.extension.getAllSongs
import com.mohammadkk.myaudioplayer.model.Songs
import java.util.*

class ArtistsFragment : BaseFragment() {
    private lateinit var binding: FragmentArtistsBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentArtistsBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        runTimeViewLoader()
        binding.artistsGridView.layoutManager = GridLayoutManager(context, if (isPortraitScreen()) 2 else 4)
    }
    override fun runTimeViewLoader() {
        val tempSongs = ArrayList<Songs>()
        requireContext().getAllAlbum().forEach {
            tempSongs.addAll(requireContext().getAllSongs(it.id))
        }
        compareSongs(tempSongs)
        val adapter = ArtistGridAdapter(requireContext(), tempSongs)
        adapter.setOnClickItemViewArtist {
            Intent(context, PlayerActivity::class.java).apply {
                putExtra("positionStart", it)
                putExtra("songs_list", tempSongs)
                MainActivity.isFadeActivity = false
                MainActivity.isRestartActivity = true
                startActivity(this)
            }
        }
        binding.artistsGridView.adapter = adapter
    }
}