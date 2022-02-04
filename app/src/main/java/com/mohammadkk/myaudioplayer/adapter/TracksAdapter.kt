package com.mohammadkk.myaudioplayer.adapter

import android.content.Context
import android.content.res.ColorStateList
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SortedList
import com.mohammadkk.myaudioplayer.R
import com.mohammadkk.myaudioplayer.databinding.TrackItemsBinding
import com.mohammadkk.myaudioplayer.extension.albumIdToArt
import com.mohammadkk.myaudioplayer.extension.formatTimeMusic
import com.mohammadkk.myaudioplayer.extension.inflater
import com.mohammadkk.myaudioplayer.model.Track

class TracksAdapter(private val context: Context) : RecyclerView.Adapter<TracksAdapter.TracksHolder>() {
    private val trackItems = SortedList(Track::class.java, object : SortedList.Callback<Track>() {
        override fun compare(o1: Track?, o2: Track?): Int {
            if (o1 != null && o2 != null) {
                return o1.title.compareTo(o2.title, true)
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
        override fun areContentsTheSame(oldItem: Track?, newItem: Track?): Boolean {
            return oldItem?.equals(newItem) ?: false
        }
        override fun areItemsTheSame(item1: Track?, item2: Track?): Boolean {
            return item1?.equals(item2) ?: false
        }
    })
    private var onItemClick: ((position: Int) -> Unit)? = null

    class TracksHolder(binding: TrackItemsBinding) : RecyclerView.ViewHolder(binding.root) {
        val trackImage = binding.imgArtTrackItem
        val trackTitle = binding.tvTitleTrackItem
        val trackAlbum = binding.tvAlbumTrackItem
        val trackDuration = binding.tvDurationTrackItem
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TracksHolder {
        return TracksHolder(TrackItemsBinding.inflate(context.inflater, parent, false))
    }
    override fun onBindViewHolder(holder: TracksHolder, position: Int) {
        val track = trackItems[position]
        track.albumId.albumIdToArt(context) { art ->
            if (art != null) {
                holder.trackImage.scaleType = ImageView.ScaleType.CENTER_CROP
                holder.trackImage.setImageBitmap(art)
                holder.trackImage.imageTintList = null
            } else {
                holder.trackImage.scaleType = ImageView.ScaleType.CENTER_INSIDE
                holder.trackImage.setImageResource(R.drawable.ic_track)
                holder.trackImage.imageTintList = ColorStateList.valueOf(
                    ContextCompat.getColor(
                        context, R.color.cyan_50
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
    fun setOnItemClickListener(listener:(position: Int) -> Unit) {
        onItemClick = listener
    }
    fun refresh() {
        for (i in 0 until trackItems.size())
            notifyItemChanged(i)
    }
    fun clear() {
        trackItems.beginBatchedUpdates()
        while (trackItems.size() > 0) trackItems.removeItemAt(trackItems.size() -1)
        trackItems.endBatchedUpdates()
    }
    fun addAll(items: List<Track>) {
        trackItems.beginBatchedUpdates()
        items.forEach { trackItems.add(it) }
        trackItems.endBatchedUpdates()
    }
    fun getAllTracks(): ArrayList<Track> {
        val tracks = arrayListOf<Track>()
        for (i in 0 until trackItems.size()) {
            tracks.add(trackItems[i])
        }
        return tracks
    }
    override fun getItemCount(): Int {
        return trackItems.size()
    }
}