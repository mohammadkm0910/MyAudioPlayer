package com.mohammadkk.myaudioplayer.fragment

import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.mohammadkk.myaudioplayer.extension.isPortraitScreen

abstract class BaseFragment : Fragment() {
    abstract fun rescanDevice()

    protected fun initializeLayout(recyclerView: RecyclerView, count: Int = 1) {
        val mCount = if (requireActivity().isPortraitScreen) count else count * 2
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = GridLayoutManager(requireContext(), mCount)
        recyclerView.isFocusableInTouchMode = true
    }
    protected fun stopRefreshing(swiper: SwipeRefreshLayout) {
        Handler(Looper.getMainLooper()).postDelayed({
            swiper.isRefreshing = false
        }, 150)
    }
    protected abstract fun initializeListAdapter()
    protected abstract fun onItemClickForList(position: Int)
}