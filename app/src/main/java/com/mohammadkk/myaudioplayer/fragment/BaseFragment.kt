package com.mohammadkk.myaudioplayer.fragment

import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mohammadkk.myaudioplayer.extension.isPortraitScreen

abstract class BaseFragment : Fragment() {
    abstract fun rescanDevice()

    protected fun initializeLayout(recyclerView: RecyclerView, count: Int = 1) {
        val mCount = if (requireActivity().isPortraitScreen) count else count * 2
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = GridLayoutManager(requireContext(), mCount)
        recyclerView.isFocusableInTouchMode = true
    }
    protected abstract fun initializeListAdapter()
    protected abstract fun onItemClickForList(position: Int)
}