package com.mohammadkk.myaudioplayer.adapter

import android.content.Context
import android.content.res.ColorStateList
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SortedList
import com.mohammadkk.myaudioplayer.R
import com.mohammadkk.myaudioplayer.databinding.AlbumItemsBinding
import com.mohammadkk.myaudioplayer.extension.albumIdToArt
import com.mohammadkk.myaudioplayer.extension.inflater
import com.mohammadkk.myaudioplayer.model.Album

class AlbumsAdapter(private val context: Context) : RecyclerView.Adapter<AlbumsAdapter.AlbumsHolder>() {
    private val albumItems = SortedList(Album::class.java, object : SortedList.Callback<Album>() {
        override fun compare(o1: Album?, o2: Album?): Int {
            if (o1 != null && o2 != null) {
                return o1.name.compareTo(o2.name, true)
            }
            return 0
        }
        override fun onInserted(position: Int, count: Int) {
            notifyItemRangeInserted(position, count)
        }
        override fun onRemoved(position: Int, count: Int) {
            notifyItemRangeRemoved(position, count)
        }
        override fun onMoved(fromPosition: Int, toPosition: Int) {
            notifyItemMoved(fromPosition, toPosition)
        }
        override fun onChanged(position: Int, count: Int) {
            notifyItemRangeChanged(position, count)
        }
        override fun areContentsTheSame(oldItem: Album?, newItem: Album?): Boolean {
            return oldItem?.equals(newItem) ?: false
        }
        override fun areItemsTheSame(item1: Album?, item2: Album?): Boolean {
            return item1?.equals(item2) ?: false
        }
    })
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
        val album = albumItems[position]
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
    fun setOnItemClickListener(listener:(position:Int)->Unit) {
        onItemClick = listener
    }
    fun refresh() {
        for (i in 0 until albumItems.size())
            notifyItemChanged(i)
    }
    fun clear() {
        albumItems.beginBatchedUpdates()
        while (albumItems.size() > 0) albumItems.removeItemAt(albumItems.size() -1)
        albumItems.endBatchedUpdates()
    }
    fun addAll(items: List<Album>) {
        albumItems.beginBatchedUpdates()
        items.forEach { albumItems.add(it) }
        albumItems.endBatchedUpdates()
    }
    fun getAlbum(position: Int): Album {
        return albumItems[position]
    }
    override fun getItemCount(): Int {
        return albumItems.size()
    }
}