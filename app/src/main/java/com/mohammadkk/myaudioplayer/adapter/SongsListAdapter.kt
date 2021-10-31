package com.mohammadkk.myaudioplayer.adapter

import android.app.Activity
import com.mohammadkk.myaudioplayer.model.Songs
import androidx.recyclerview.widget.RecyclerView
import com.mohammadkk.myaudioplayer.adapter.SongsListAdapter.SongsHolder
import android.view.ViewGroup
import android.view.LayoutInflater
import com.mohammadkk.myaudioplayer.R
import com.mohammadkk.myaudioplayer.helper.MusicUtil
import android.content.res.ColorStateList
import android.net.Uri
import android.view.View
import android.widget.ImageView
import androidx.core.content.ContextCompat
import com.google.android.material.imageview.ShapeableImageView
import android.widget.TextView

class SongsListAdapter(private val activity: Activity, private val songFiles: List<Songs>) :
    RecyclerView.Adapter<SongsHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongsHolder {
        return SongsHolder(
            LayoutInflater.from(activity).inflate(R.layout.song_items, parent, false)
        )
    }
    override fun onBindViewHolder(holder: SongsHolder, position: Int) {
        MusicUtil.createThread {
            val cover = MusicUtil.getAlbumCoverByUri(
                activity, Uri.parse(
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
        holder.itemView.setOnClickListener {
            onClickItemViewSong(position)
        }
    }

    fun setOnClickItemViewSong(listener:(position: Int) -> Unit) {
        onClickItemViewSong = listener
    }

    override fun getItemCount(): Int {
        return songFiles.size
    }

    class SongsHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val itemCoverSong: ShapeableImageView = itemView.findViewById(R.id.itemCoverSong)
        val itemTitleSong: TextView = itemView.findViewById(R.id.itemTitleSong)
        val itemArtistSong: TextView = itemView.findViewById(R.id.itemArtistSong)
    }
    companion object {
        private lateinit var onClickItemViewSong:(position: Int) -> Unit
    }
}