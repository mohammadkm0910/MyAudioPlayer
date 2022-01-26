package com.mohammadkk.myaudioplayer.adapter

import android.content.Context
import android.content.res.ColorStateList
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.mohammadkk.myaudioplayer.R
import com.mohammadkk.myaudioplayer.databinding.AlbumItemsBinding
import com.mohammadkk.myaudioplayer.extension.albumIdToArt
import com.mohammadkk.myaudioplayer.extension.inflater
import com.mohammadkk.myaudioplayer.model.Album

class AlbumsAdapter(private val context: Context) : RecyclerView.Adapter<AlbumsAdapter.AlbumsHolder>() {
    private val albumList: MutableList<Album> = ArrayList()
    private var onItemClick : ((position:Int)->Unit)? = null

    class AlbumsHolder(binding: AlbumItemsBinding) : RecyclerView.ViewHolder(binding.root) {
        var albumCover = binding.imgCoverItem
        var albumName = binding.tvNameItem
        var trackCountByAlbum = binding.tvTrackCountItem
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlbumsHolder {
        return AlbumsHolder(AlbumItemsBinding.inflate(context.inflater, parent, false))
    }
    override fun onBindViewHolder(holder: AlbumsHolder, position: Int) {
        val album = albumList[position]
        album.id.albumIdToArt(context) { art ->
            if (art != null) {
                holder.albumCover.setImageBitmap(art)
                holder.albumCover.imageTintList = null
                holder.albumCover.scaleType = ImageView.ScaleType.FIT_XY
            } else {
                holder.albumCover.setImageResource(R.drawable.ic_albums)
                holder.albumCover.imageTintList = ColorStateList.valueOf(
                    ContextCompat.getColor(
                        context, R.color.pink_500
                    )
                )
                holder.albumCover.scaleType = ImageView.ScaleType.CENTER
            }
        }
        holder.albumName.text = album.name
        holder.trackCountByAlbum.text = String.format("%s%s", "Tracks ", album.trackCount)
        holder.itemView.setOnClickListener {
            onItemClick?.invoke(position)
        }
    }
    fun updateAlbumList(albums: ArrayList<Album>) {
        this.albumList.clear()
        this.albumList.addAll(albums)
        refreshList()
    }
    private fun refreshList() {
        @Suppress("NotifyDataSetChanged")
        this.notifyDataSetChanged()
    }
    fun setOnItemClickListener(listener:(position:Int)->Unit) {
        onItemClick = listener
    }
    fun getAlbum(position: Int): Album {
        return albumList[position]
    }
    override fun getItemCount(): Int {
        return albumList.size
    }
}