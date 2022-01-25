package com.mohammadkk.myaudioplayer.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.mohammadkk.myaudioplayer.*
import com.mohammadkk.myaudioplayer.databinding.FragmentNowPlayerBinding
import com.mohammadkk.myaudioplayer.model.Songs
import com.mohammadkk.myaudioplayer.service.MediaService

class NowPlayerFragment : Fragment() {
    private lateinit var binding: FragmentNowPlayerBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentNowPlayerBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        updateView()
        binding.btnNowPlayPause.setOnClickListener {
            if (MediaService.isService) {
                Intent(requireContext(), NotificationReceiver::class.java).apply {
                    action = AudioApp.ACTION_PLAY
                    requireContext().sendBroadcast(this)
                }
            }
        }
        binding.nowPlayer.setOnClickListener {
            if (MediaService.isService) {
                val playIntent = Intent(requireContext(), PlayerActivity::class.java)
                playIntent.putExtra("positionStart", buildCacheApp.positionService)
                MainActivity.isRestartActivity = false
                MainActivity.isFadeActivity = true
                startActivity(playIntent)
            }
        }
    }
    internal fun updateView() {
        if (MediaService.mediaList.isNotEmpty()) {
            binding.tvNowTitle.text = MediaService.mediaList[buildCacheApp.positionService].title
            binding.tvNowArtist.text = MediaService.mediaList[buildCacheApp.positionService].artist
            binding.btnNowPlayPause.setImageResource(buildCacheApp.iconPausedService)
        }
    }
    override fun onResume() {
        super.onResume()
        listener = object : INowPlay {
            override fun updateNowPlay(icon: Int, songs: Songs) {
                if (MediaService.mediaList.isNotEmpty()) {
                    binding.tvNowTitle.text = songs.title
                    binding.tvNowArtist.text = songs.artist
                    binding.btnNowPlayPause.setImageResource(icon)
                }
            }
        }
    }
    companion object {
        internal var listener: INowPlay? = null
    }
    interface INowPlay {
        fun updateNowPlay(icon: Int, songs: Songs)
    }
}