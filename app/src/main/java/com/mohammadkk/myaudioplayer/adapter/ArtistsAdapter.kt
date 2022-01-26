package com.mohammadkk.myaudioplayer.adapter

import android.content.Context
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mohammadkk.myaudioplayer.adapter.ArtistsAdapter.ArtistsHolder
import com.mohammadkk.myaudioplayer.databinding.ArtistItemsBinding
import com.mohammadkk.myaudioplayer.extension.inflater
import com.mohammadkk.myaudioplayer.model.Artist

class ArtistsAdapter(private val context: Context) : RecyclerView.Adapter<ArtistsHolder>() {
    private val artistList: MutableList<Artist> = ArrayList()
    private var onItemClick: ((position: Int)->Unit)? = null

    class ArtistsHolder(binding: ArtistItemsBinding) : RecyclerView.ViewHolder(binding.root) {
        val artistName = binding.tvNameItem
        val trackCountByArtist = binding.tvTrackCountItem
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArtistsHolder {
        return ArtistsHolder(ArtistItemsBinding.inflate(context.inflater, parent, false))
    }
    override fun onBindViewHolder(holder: ArtistsHolder, position: Int) {
        val artist = artistList[position]
        holder.artistName.text = artist.name
        holder.trackCountByArtist.text = String.format("%s%s", "Tracks ", artist.trackCount)
        holder.itemView.setOnClickListener {
            onItemClick?.invoke(position)
        }
    }
    fun updateArtistList(artists: ArrayList<Artist>) {
        this.artistList.clear()
        this.artistList.addAll(artists)
        refreshList()
    }
    private fun refreshList() {
        @Suppress("NotifyDataSetChanged")
        this.notifyDataSetChanged()
    }
    fun setOnItemClickListener(listener:(position: Int)->Unit) {
        onItemClick = listener
    }
    fun getArtist(position: Int): Artist {
        return artistList[position]
    }
    override fun getItemCount(): Int {
        return artistList.size
    }
}