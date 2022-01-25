package com.mohammadkk.myaudioplayer.adapter

import android.content.Context
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mohammadkk.myaudioplayer.adapter.ArtistGridAdapter.ArtistHolder
import com.mohammadkk.myaudioplayer.databinding.ArtistItemsBinding
import com.mohammadkk.myaudioplayer.extension.inflater
import com.mohammadkk.myaudioplayer.model.Songs

class ArtistGridAdapter(private val context: Context, private val artistFiles: List<Songs>) :
    RecyclerView.Adapter<ArtistHolder>() {
    private var onClickItemViewArtist: ((position: Int)->Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArtistHolder {
        return ArtistHolder(ArtistItemsBinding.inflate(context.inflater, parent, false))
    }

    override fun onBindViewHolder(holder: ArtistHolder, position: Int) {
        holder.itemNameArtist.text = artistFiles[position].artist
        holder.container.setOnClickListener {
            onClickItemViewArtist?.invoke(position)
        }
    }
    override fun getItemCount(): Int {
        return artistFiles.size
    }

    class ArtistHolder(binding: ArtistItemsBinding) : RecyclerView.ViewHolder(binding.root) {
        val itemNameArtist = binding.itemNameArtist
        val container = binding.root
    }

    fun setOnClickItemViewArtist(listener:(position: Int)->Unit) {
        onClickItemViewArtist = listener
    }
}