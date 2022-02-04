package com.mohammadkk.myaudioplayer.extension

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.view.LayoutInflater
import androidx.core.content.ContextCompat
import androidx.loader.content.CursorLoader
import com.mohammadkk.myaudioplayer.helper.BuildUtil
import com.mohammadkk.myaudioplayer.helper.MusicUtil

fun Context.hasPermission(permission: String =  Manifest.permission.READ_EXTERNAL_STORAGE): Boolean {
    if (BuildUtil.isMarshmallowPlus()) {
        val base = ContextCompat.checkSelfPermission(this, permission)
        return base == PackageManager.PERMISSION_GRANTED
    }
    return true
}
fun Context.queryCursor(
    uri: Uri,
    projection: Array<String>? = null,
    selection: String? = null,
    selectionArgs: Array<String>? = null,
    sortOrder: String? = null,
    showError: Boolean = false,
    callback: (cursor: Cursor) -> Unit
) {
    val cursor =
        CursorLoader(this, uri, projection, selection, selectionArgs, sortOrder).loadInBackground()
    cursor?.use {
        try {
            if (cursor.moveToFirst()) {
                do {
                    callback(cursor)
                } while (cursor.moveToNext())
            }
        } catch (e: Exception) {
            if (showError) {
                e.printStackTrace()
            }
        }
    }
}
fun Context.getCoverTrack(uri: Uri): Bitmap? {
    var cover: Bitmap? = null
    try {
        val mmr = MediaMetadataRetriever()
        mmr.setDataSource(this, uri)
        val art = mmr.embeddedPicture
        if (art != null) {
            cover = BitmapFactory.decodeByteArray(art, 0, art.size, BitmapFactory.Options())
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return cover
}
val Context.musicUtil: MusicUtil get() = MusicUtil.newInstance(this)

val Context.inflater: LayoutInflater get() = LayoutInflater.from(this)

val Context.isPortraitScreen get() = resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT