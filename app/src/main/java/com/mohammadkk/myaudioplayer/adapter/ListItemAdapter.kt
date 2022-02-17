package com.mohammadkk.myaudioplayer.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mohammadkk.myaudioplayer.databinding.ListItemsBinding
import com.mohammadkk.myaudioplayer.extension.inflater

class ListItemAdapter(private val items: MutableList<String>) : RecyclerView.Adapter<ListItemAdapter.ListHolder>() {
    class ListHolder(binding: ListItemsBinding) : RecyclerView.ViewHolder(binding.root) {
        val itemList = binding.tvItemList
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListHolder {
        return ListHolder(ListItemsBinding.inflate(parent.context.inflater, parent, false))
    }
    override fun onBindViewHolder(holder: ListHolder, position: Int) {
        holder.itemList.text = items[position]
    }
    override fun getItemCount(): Int {
        return items.size
    }
}