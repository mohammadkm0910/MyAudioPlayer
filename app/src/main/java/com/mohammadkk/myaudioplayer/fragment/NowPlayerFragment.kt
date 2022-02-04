package com.mohammadkk.myaudioplayer.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.mohammadkk.myaudioplayer.AudioApp
import com.mohammadkk.myaudioplayer.MainActivity
import com.mohammadkk.myaudioplayer.PlayerActivity
import com.mohammadkk.myaudioplayer.buildCacheApp
import com.mohammadkk.myaudioplayer.databinding.FragmentNowPlayerBinding
import com.mohammadkk.myaudioplayer.model.Track
import com.mohammadkk.myaudioplayer.service.MediaService
import com.mohammadkk.myaudioplayer.service.NotificationReceiver

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
            if (MediaService.getIsExists()) {
                Intent(requireContext(), NotificationReceiver::class.java).apply {
                    action = AudioApp.ACTION_PLAY
                    requireContext().sendBroadcast(this)
                }
            }
        }
        binding.nowPlayer.setOnClickListener {
            if (MediaService.getIsExists()) {
                val playIntent = Intent(requireContext(), PlayerActivity::class.java)
                playIntent.putExtra("positionStart", buildCacheApp.globalTrackIndexCaller)
                MainActivity.isRestartActivity = false
                MainActivity.isFadeActivity = true
                startActivity(playIntent)
            }
        }
    }
    internal fun updateView() {
        binding.tvNowTitle.text = buildCacheApp.titleTextCaller
        binding.tvNowArtist.text = buildCacheApp.artistTextCaller
        binding.btnNowPlayPause.setImageResource(buildCacheApp.iconPausedCaller)
    }
    override fun onResume() {
        super.onResume()
        listener = object : INowPlay {
            override fun updateNowPlay(icon: Int, songs: Track) {
                if (MediaService.getMediaList().isNotEmpty()) {
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
        fun updateNowPlay(icon: Int, songs: Track)
    }
}