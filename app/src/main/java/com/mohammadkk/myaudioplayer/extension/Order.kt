package com.mohammadkk.myaudioplayer.extension

import android.content.ContentUris
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.AnimatedVectorDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.provider.MediaStore
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.mohammadkk.myaudioplayer.R
import com.mohammadkk.myaudioplayer.fragment.BaseFragment
import com.mohammadkk.myaudioplayer.helper.BuildUtil
import com.mohammadkk.myaudioplayer.helper.Constants
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*


fun FragmentManager.changeFragment(fragment: BaseFragment, tagFragment: String) {
    var current = findFragmentByTag(tagFragment)
    beginTransaction()
        .apply {
            primaryNavigationFragment?.let { hide(it) }
            if (current == null) {
                current = fragment
                add(R.id.fragmentContainer, current!!, tagFragment)
            } else {
                show(current!!)
            }
        }
        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
        .setPrimaryNavigationFragment(current)
        .setReorderingAllowed(true)
        .commitAllowingStateLoss()
}
fun FloatingActionButton.setResVectorDrawable(anim: Drawable?) {
    setImageDrawable(anim)
    val draw = drawable
    if (draw is AnimatedVectorDrawableCompat) {
        draw.start()
    } else if (draw is AnimatedVectorDrawable) {
        draw.start()
    }
}
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
fun Long.albumIdToArt(context: Context, callback: (art: Bitmap?) -> Unit) {
    val albumId = this
    CoroutineScope(Dispatchers.IO).launch {
        val uri = ContentUris.withAppendedId(Constants.ALBUM_ART, albumId)
        val bitmap = getAlbumCoverByUri(context, uri)
        withContext(Dispatchers.Main) {
            callback(bitmap)
        }
    }
}
private fun getAlbumCoverByUri(context: Context, uri: Uri): Bitmap? {
    var cover: Bitmap? = null
    try {
        val pfd = context.contentResolver.openFileDescriptor(uri, "r")
        pfd?.use {
            val fd = pfd.fileDescriptor
            cover = BitmapFactory.decodeFileDescriptor(fd)
        }
    } catch (e: java.lang.Exception) {
        e.printStackTrace()
    }
    return cover
}