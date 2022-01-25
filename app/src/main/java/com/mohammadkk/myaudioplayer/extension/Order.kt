package com.mohammadkk.myaudioplayer.extension

import android.content.ContentUris
import android.net.Uri
import android.provider.MediaStore
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.mohammadkk.myaudioplayer.R
import com.mohammadkk.myaudioplayer.fragment.BaseFragment
import com.mohammadkk.myaudioplayer.helper.BuildUtil
import java.util.*

fun Int.formatTimeMusic(): String {
    val seconds = this / 1000
    val h = seconds / (60 * 60) % 24
    val m = seconds / 60 % 60
    val s = seconds % 60
    return if (h > 0) {
        String.format(Locale.ENGLISH, "%02d:%02d:%02d", h, m, s)
    } else {
        String.format(Locale.ENGLISH, "%02d:%02d", m, s)
    }
}
fun Long.toContentUri(): Uri {
    val uri = if (BuildUtil.isQPlus()) {
        MediaStore.Audio.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
    } else MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
    return ContentUris.withAppendedId(uri, this)
}
fun FragmentManager.replaceFragment(fragment: BaseFragment, tagFragment: String) {
    val fragTrans = beginTransaction()
    fragTrans.replace(R.id.fragmentContainer, fragment, tagFragment)
    fragTrans.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
    fragTrans.commit()
}