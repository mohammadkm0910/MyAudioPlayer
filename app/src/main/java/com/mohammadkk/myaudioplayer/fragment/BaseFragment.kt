package com.mohammadkk.myaudioplayer.fragment

import android.content.res.Configuration
import androidx.fragment.app.Fragment
import com.mohammadkk.myaudioplayer.model.Albums
import com.mohammadkk.myaudioplayer.model.Songs

abstract class BaseFragment : Fragment() {
    abstract fun runTimeViewLoader()

    protected fun compareSongs(list: ArrayList<Songs>) {
        list.sortWith { o1, o2 -> o1.title.compareTo(o2.title) }
    }
    protected fun compareAlbums(list: ArrayList<Albums>) {
        list.sortWith { o1, o2 -> o1.name.compareTo(o2.name) }
    }
    protected fun isPortraitScreen(): Boolean {
        return requireActivity().resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT
    }
}