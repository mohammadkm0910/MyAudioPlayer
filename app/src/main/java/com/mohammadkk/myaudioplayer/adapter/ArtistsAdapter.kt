package com.mohammadkk.myaudioplayer.adapter

import android.content.Context
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SortedList
import com.mohammadkk.myaudioplayer.adapter.ArtistsAdapter.ArtistsHolder
import com.mohammadkk.myaudioplayer.databinding.ArtistItemsBinding
import com.mohammadkk.myaudioplayer.extension.inflater
import com.mohammadkk.myaudioplayer.model.Artist

class ArtistsAdapter(private val context: Context) : RecyclerView.Adapter<ArtistsHolder>() {
    private val artistItems = SortedList(Artist::class.java, object : SortedList.Callback<Artist>() {
        override fun compare(o1: Artist?, o2: Artist?): Int {
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
        override fun areContentsTheSame(oldItem: Artist?, newItem: Artist?): Boolean {
            return oldItem?.equals(newItem) ?: false
        }
        override fun areItemsTheSame(item1: Artist?, item2: Artist?): Boolean {
            return item1?.equals(item2) ?: false
        }
    })
    private var onItemClick: ((position: Int)->Unit)? = null

    class ArtistsHolder(binding: ArtistItemsBinding) : RecyclerView.ViewHolder(binding.root) {
        val artistName = binding.tvNameItem
        val trackCountByArtist = binding.tvTrackCountItem
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArtistsHolder {
        return ArtistsHolder(ArtistItemsBinding.inflate(context.inflater, parent, false))
    }
    override fun onBindViewHolder(holder: ArtistsHolder, position: Int) {
        val artist = artistItems[position]
        holder.artistName.text = artist.name
        holder.trackCountByArtist.text = String.format("%s%s", "Tracks ", artist.trackCount)
        holder.itemView.setOnClickListener {
            onItemClick?.invoke(position)
        }
    }
    fun setOnItemClickListener(listener:(position: Int)->Unit) {
        onItemClick = listener
    }
    fun refresh() {
        for (i in 0 until artistItems.size())
            notifyItemChanged(i)
    }
    fun clear() {
        artistItems.beginBatchedUpdates()
        while (artistItems.size() > 0) artistItems.removeItemAt(artistItems.size() -1)
        artistItems.endBatchedUpdates()
    }
    fun addAll(items: List<Artist>) {
        artistItems.beginBatchedUpdates()
        items.forEach { artistItems.add(it) }
        artistItems.endBatchedUpdates()
    }
    fun getArtist(position: Int): Artist {
        return artistItems[position]
    }
    override fun getItemCount(): Int {
        return artistItems.size()
    }
}