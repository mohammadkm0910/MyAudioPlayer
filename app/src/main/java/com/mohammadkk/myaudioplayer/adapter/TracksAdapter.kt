package com.mohammadkk.myaudioplayer.adapter

import android.content.Context
import android.content.res.ColorStateList
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.mohammadkk.myaudioplayer.R
import com.mohammadkk.myaudioplayer.databinding.TrackItemsBinding
import com.mohammadkk.myaudioplayer.extension.albumIdToArt
import com.mohammadkk.myaudioplayer.extension.formatTimeMusic
import com.mohammadkk.myaudioplayer.extension.inflater
import com.mohammadkk.myaudioplayer.model.Track

class TracksAdapter(private val context: Context) : RecyclerView.Adapter<TracksAdapter.TracksHolder>() {
    private val tackList: MutableList<Track> = ArrayList()
    private var onItemClick: ((position: Int) -> Unit)? = null

    class TracksHolder(binding: TrackItemsBinding) : RecyclerView.ViewHolder(binding.root) {
        val trackImage = binding.imgCoverItem
        val trackTitle = binding.tvTitleItem
        val trackAlbum = binding.tvAlbumItem
        val trackDuration = binding.tvDurationItem
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TracksHolder {
        return TracksHolder(TrackItemsBinding.inflate(context.inflater, parent, false))
    }
    override fun onBindViewHolder(holder: TracksHolder, position: Int) {
        val track = tackList[position]
        track.albumId.albumIdToArt(context) { art ->
            if (art != null) {
                holder.trackImage.scaleType = ImageView.ScaleType.CENTER_CROP
                holder.trackImage.setImageBitmap(art)
                holder.trackImage.imageTintList = null
            } else {
                holder.trackImage.scaleType = ImageView.ScaleType.CENTER_INSIDE
                holder.trackImage.setImageResource(R.drawable.ic_songs)
                holder.trackImage.imageTintList = ColorStateList.valueOf(
                    ContextCompat.getColor(
                        context, R.color.pink_500
                    )
                )
            }
        }
        holder.trackTitle.text = track.title
        holder.trackAlbum.text = track.album
        holder.trackDuration.text = (track.duration * 1000).formatTimeMusic()
        holder.itemView.setOnClickListener {
            onItemClick?.invoke(position)
        }
    }
    fun updateTrackList(tracks: ArrayList<Track>) {
        this.tackList.clear()
        this.tackList.addAll(tracks)
        refreshList()
    }
    private fun refreshList() {
        @Suppress("NotifyDataSetChanged")
        this.notifyDataSetChanged()
    }
    fun setOnItemClickListener(listener:(position: Int) -> Unit) {
        onItemClick = listener
    }
    fun getTracks(): ArrayList<Track> {
        val list = arrayListOf<Track>()
        list.addAll(tackList)
        return list
    }
    override fun getItemCount(): Int {
        return tackList.size
    }
}