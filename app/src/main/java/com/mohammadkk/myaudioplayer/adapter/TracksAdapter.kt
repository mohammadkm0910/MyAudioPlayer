package com.mohammadkk.myaudioplayer.adapter

import android.app.Activity
import android.content.IntentSender
import android.content.res.ColorStateList
import android.net.Uri
import android.provider.MediaStore
import android.view.Menu
import android.view.MenuItem
import android.view.ViewGroup
import android.widget.ImageView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.view.ActionMode
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SortedList
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.mohammadkk.myaudioplayer.R
import com.mohammadkk.myaudioplayer.UIController
import com.mohammadkk.myaudioplayer.databinding.TrackItemsBinding
import com.mohammadkk.myaudioplayer.extension.*
import com.mohammadkk.myaudioplayer.helper.BuildUtil
import com.mohammadkk.myaudioplayer.model.Track
import java.io.File
import kotlin.collections.set

class TracksAdapter(private val activity: Activity) : RecyclerView.Adapter<TracksAdapter.TracksHolder>() {
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
    private val runIntentSender: ActivityResultLauncher<IntentSenderRequest>
    private val selectedItems = hashMapOf<Int, Track>()
    private var onItemClick: ((position: Int) -> Unit)? = null
    private var onItemLongClick: ((position: Int) -> Unit)? = null
    init {
        runIntentSender = (activity as FragmentActivity).registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) {
            val listNotExists = getAllTracks().filter { !File(it.path).exists() }
            listNotExists.forEach { trackItems.remove(it) }
            refresh()
        }
    }
    class TracksHolder(binding: TrackItemsBinding) : RecyclerView.ViewHolder(binding.root) {
        val trackImage = binding.imgArtTrackItem
        val trackTitle = binding.tvTitleTrackItem
        val trackAlbum = binding.tvAlbumTrackItem
        val trackDuration = binding.tvDurationTrackItem
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TracksHolder {
        return TracksHolder(TrackItemsBinding.inflate(activity.inflater, parent, false))
    }
    override fun onBindViewHolder(holder: TracksHolder, position: Int) {
        val track = trackItems[position]
        track.albumId.albumIdToArt(activity) { art ->
            if (art != null) {
                holder.trackImage.scaleType = ImageView.ScaleType.CENTER_CROP
                holder.trackImage.setImageBitmap(art)
                holder.trackImage.imageTintList = null
            } else {
                holder.trackImage.scaleType = ImageView.ScaleType.CENTER_INSIDE
                holder.trackImage.setImageResource(R.drawable.ic_track)
                holder.trackImage.imageTintList = ColorStateList.valueOf(
                    ContextCompat.getColor(
                        activity, R.color.cyan_50
                    )
                )
            }
        }
        holder.trackTitle.text = track.title
        holder.trackAlbum.text = track.album
        holder.trackDuration.text = (track.duration * 1000).formatTimeMusic()
        if (selectedItems[position] == track) {
            holder.itemView.setBackgroundResource(R.drawable.background_list_items_one_selected)
        } else {
            holder.itemView.setBackgroundResource(R.drawable.background_list_items_one)
        }
        holder.itemView.setOnClickListener {
            onItemClick?.invoke(position)
        }
        holder.itemView.setOnLongClickListener {
            onItemLongClick?.invoke(position)
            true
        }
    }
    fun createActionCallback(): ActionMode.Callback {
        return object : ActionMode.Callback {
            override fun onCreateActionMode(mode: ActionMode?, menu: Menu?): Boolean {
                val menuInflater = mode?.menuInflater
                menuInflater?.inflate(R.menu.main_action, menu)
                return true
            }
            override fun onPrepareActionMode(mode: ActionMode?, menu: Menu?): Boolean {
                mode?.title = "Selected Item ${getSelectedItemCount()}"
                return true
            }
            override fun onActionItemClicked(mode: ActionMode?, item: MenuItem?): Boolean {
                when (item?.itemId) {
                    R.id.actionDelete -> deleteAction(mode)
                }
                return true
            }
            override fun onDestroyActionMode(mode: ActionMode?) {
                selectedItems.clear()
                refresh()
                uiController?.onDownActionMode()
            }
        }
    }
    fun toggleSelected(position: Int) {
        if (selectedItems.containsKey(position)) {
            selectedItems.remove(position)
        } else {
            selectedItems[position] = trackItems[position]
        }
        uiController?.onUpdateActionMode("Selected Item ${getSelectedItemCount()}")
        refresh()
    }
    private fun deleteAction(mode: ActionMode?) {
        if (getSelectedItemCount() <= 10) {
            val listUri = mutableListOf<Uri>()
            val titles = mutableListOf<String>()
            selectedItems.values.forEach { track ->
                listUri.add(track.id.toContentUri())
                titles.add(track.title)
            }
            val recyclerView = RecyclerView(activity)
            recyclerView.layoutManager = LinearLayoutManager(activity)
            recyclerView.adapter = ListItemAdapter(titles)
            MaterialAlertDialogBuilder(activity)
                .setTitle("Confirm Delete")
                .setView(recyclerView)
                .setPositiveButton(android.R.string.ok) { dialog, _ ->
                    if (BuildUtil.isRPlus()) {
                        try {
                            val pi = MediaStore.createDeleteRequest(activity.contentResolver, listUri)
                            val intentSenderRequest = IntentSenderRequest.Builder(pi).build()
                            runIntentSender.launch(intentSenderRequest)
                        } catch (e: IntentSender.SendIntentException) {
                            e.printStackTrace()
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    } else {
                        BuildUtil.buildBaseThread {
                            try {
                                selectedItems.values.forEach {
                                    File(it.path).delete()
                                    activity.deletePathStore(it.path)
                                    activity.runOnUiThread {
                                        trackItems.remove(it)
                                        notifyRefresh()
                                    }
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                    }
                    dialog.dismiss()
                    mode?.finish()
                }
                .setNegativeButton(android.R.string.cancel) { dialog, _ ->
                    dialog.dismiss()
                }
                .create()
                .show()
        } else {
            activity.showToast("Selected items can not be more than 10 items!!")
        }
    }
    private val uiController: UIController?
        get() = if (activity is UIController) {
            activity
        } else null
    fun setOnItemClickListener(listener:(position: Int) -> Unit) {
        onItemClick = listener
    }
    fun setOnItemLongClickListener(listener:(position: Int) -> Unit) {
        onItemLongClick = listener
    }
    fun refresh() {
        for (i in 0 until trackItems.size())
            notifyItemChanged(i)
    }
    private fun notifyRefresh() {
        @Suppress("NotifyDataSetChanged")
        notifyDataSetChanged()
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
    fun getSelectedItemCount(): Int {
        return selectedItems.size
    }
    override fun getItemCount(): Int {
        return trackItems.size()
    }
}