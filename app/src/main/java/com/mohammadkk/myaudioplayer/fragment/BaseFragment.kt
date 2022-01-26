package com.mohammadkk.myaudioplayer.fragment

import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mohammadkk.myaudioplayer.extension.isPortraitScreen
import com.mohammadkk.myaudioplayer.extension.musicUtil
import com.mohammadkk.myaudioplayer.helper.MusicUtil

abstract class BaseFragment : Fragment() {
    protected val musicUtil: MusicUtil get() = requireContext().musicUtil

    abstract fun runTimeViewLoader()
    protected abstract fun onItemClickForList(position: Int)

    protected fun initializeLayout(recyclerView: RecyclerView, count: Int = 1) {
        val mCount = if (requireActivity().isPortraitScreen) count else count * 2
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = GridLayoutManager(requireContext(), mCount)
        recyclerView.isFocusableInTouchMode = true
    }
}