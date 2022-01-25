package com.mohammadkk.myaudioplayer.adapter

import android.app.Activity
import android.content.res.ColorStateList
import android.net.Uri
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.mohammadkk.myaudioplayer.R
import com.mohammadkk.myaudioplayer.adapter.SongsListAdapter.SongsHolder
import com.mohammadkk.myaudioplayer.databinding.SongItemsBinding
import com.mohammadkk.myaudioplayer.extension.getAlbumCoverByUri
import com.mohammadkk.myaudioplayer.extension.inflater
import com.mohammadkk.myaudioplayer.helper.BuildUtil
import com.mohammadkk.myaudioplayer.model.Songs

class SongsListAdapter(private val activity: Activity, private val songFiles: List<Songs>) :
    RecyclerView.Adapter<SongsHolder>() {
    private var onClickItemViewSong: ((position: Int) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongsHolder {
        return SongsHolder(SongItemsBinding.inflate(activity.inflater, parent, false))
    }
    override fun onBindViewHolder(holder: SongsHolder, position: Int) {
        BuildUtil.buildBaseThread {
            val cover = activity.getAlbumCoverByUri(
                Uri.parse(
                    songFiles[position].albumArt
                )
            )
            activity.runOnUiThread {
                if (cover != null) {
                    holder.itemCoverSong.scaleType = ImageView.ScaleType.CENTER_CROP
                    holder.itemCoverSong.setImageBitmap(cover)
                    holder.itemCoverSong.imageTintList = null
                } else {
                    holder.itemCoverSong.scaleType = ImageView.ScaleType.CENTER_INSIDE
                    holder.itemCoverSong.setImageResource(R.drawable.ic_songs)
                    holder.itemCoverSong.imageTintList = ColorStateList.valueOf(
                        ContextCompat.getColor(
                            activity, R.color.pink_500
                        )
                    )
                }
            }
        }
        holder.itemTitleSong.text = songFiles[position].title
        holder.itemArtistSong.text = songFiles[position].artist
        holder.container.setOnClickListener {
            onClickItemViewSong?.invoke(position)
        }
    }
    fun setOnClickItemViewSong(listener:(position: Int) -> Unit) {
        onClickItemViewSong = listener
    }
    override fun getItemCount(): Int {
        return songFiles.size
    }
    class SongsHolder(binding: SongItemsBinding) : RecyclerView.ViewHolder(binding.root) {
        val itemCoverSong = binding.itemCoverSong
        val itemTitleSong = binding.itemTitleSong
        val itemArtistSong = binding.itemArtistSong
        val container = binding.root
    }
}