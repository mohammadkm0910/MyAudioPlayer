package com.mohammadkk.myaudioplayer.adapter

import android.app.Activity
import com.mohammadkk.myaudioplayer.model.Songs
import androidx.recyclerview.widget.RecyclerView
import com.mohammadkk.myaudioplayer.adapter.AlbumGridAdapter.AlbumHolder
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

class AlbumGridAdapter(private val activity: Activity, private val albumFiles: List<Songs>) :
    RecyclerView.Adapter<AlbumHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlbumHolder {
        return AlbumHolder(
            LayoutInflater.from(activity).inflate(R.layout.album_items, parent, false)
        )
    }

    override fun onBindViewHolder(holder: AlbumHolder, position: Int) {
        MusicUtil.createThread {
            val cover = MusicUtil.getAlbumCoverByUri(
                activity, Uri.parse(
                    albumFiles[position].albumArt
                )
            )
            activity.runOnUiThread {
                if (cover != null) {
                    holder.itemCoverAlbum.setImageBitmap(cover)
                    holder.itemCoverAlbum.imageTintList = null
                    holder.itemCoverAlbum.scaleType = ImageView.ScaleType.CENTER_CROP
                } else {
                    holder.itemCoverAlbum.setImageResource(R.drawable.ic_albums)
                    holder.itemCoverAlbum.imageTintList = ColorStateList.valueOf(
                        ContextCompat.getColor(
                            activity, R.color.pink_500
                        )
                    )
                    holder.itemCoverAlbum.scaleType = ImageView.ScaleType.CENTER
                }
            }
        }
        holder.itemNameAlbum.text = albumFiles[position].album
        holder.itemView.setOnClickListener {
            onClickItemViewAlbum(position)
        }
    }

    override fun getItemCount(): Int {
        return albumFiles.size
    }

    class AlbumHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var itemCoverAlbum: ShapeableImageView = itemView.findViewById(R.id.itemCoverAlbum)
        var itemNameAlbum: TextView = itemView.findViewById(R.id.itemNameAlbum)
    }

    fun setOnClickItemViewAlbum(lisener:(postion:Int)->Unit) {
        onClickItemViewAlbum = lisener
    }

    companion object {
        private lateinit var onClickItemViewAlbum:(position:Int)->Unit
    }
}