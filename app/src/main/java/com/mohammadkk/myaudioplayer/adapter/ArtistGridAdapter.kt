package com.mohammadkk.myaudioplayer.adapter

import android.content.Context
import com.mohammadkk.myaudioplayer.model.Songs
import androidx.recyclerview.widget.RecyclerView
import com.mohammadkk.myaudioplayer.adapter.ArtistGridAdapter.ArtistHolder
import android.view.ViewGroup
import android.view.LayoutInflater
import android.view.View
import com.mohammadkk.myaudioplayer.R
import android.widget.TextView

class ArtistGridAdapter(private val context: Context, private val artistFiles: List<Songs>) :
    RecyclerView.Adapter<ArtistHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArtistHolder {
        return ArtistHolder(
            LayoutInflater.from(context).inflate(R.layout.artist_items, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ArtistHolder, position: Int) {
        holder.itemNameArtist.text = artistFiles[position].artist
        holder.itemView.setOnClickListener {
            onClickItemViewArtist(position)
        }
    }

    override fun getItemCount(): Int {
        return artistFiles.size
    }

    class ArtistHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var itemNameArtist: TextView = itemView.findViewById(R.id.itemNameArtist)
    }

    fun setOnClickItemViewArtist(listener:(position: Int)->Unit) {
        onClickItemViewArtist = listener
    }
    companion object {
        private lateinit var onClickItemViewArtist:(position: Int)->Unit
    }
}