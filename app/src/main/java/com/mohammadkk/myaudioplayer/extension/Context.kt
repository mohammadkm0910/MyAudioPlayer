package com.mohammadkk.myaudioplayer.extension

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.media.MediaScannerConnection
import android.net.Uri
import android.view.LayoutInflater
import androidx.core.content.ContextCompat
import com.mohammadkk.myaudioplayer.helper.BuildUtil
import java.io.File

fun Context.hasPermission(permission: String =  Manifest.permission.READ_EXTERNAL_STORAGE): Boolean {
    if (BuildUtil.isMarshmallowPlus()) {
        val base = ContextCompat.checkSelfPermission(this, permission)
        return base == PackageManager.PERMISSION_GRANTED
    }
    return true
}
fun Context.getInternalStorage(): File {
    val dir = getExternalFilesDirs(null)[0]
    var path = dir.absolutePath
    path = path.substring(0, path.indexOf("Android/data"))
    path = path.trimEnd('/')
    return File(path)
}
fun Context.rescanPaths(paths: Array<String>, callback: (() -> Unit)? = null) {
    if (paths.isEmpty()) {
        callback?.invoke()
        return
    }
    var cnt = paths.size
    MediaScannerConnection.scanFile(applicationContext, paths, null) { _, _ ->
        if (--cnt == 0) {
            callback?.invoke()
        }
    }
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
    val cursor = contentResolver.query(uri, projection, selection, selectionArgs, sortOrder)
    try {
        cursor?.use {
            if (cursor.moveToFirst()) {
                do {
                    callback(cursor)
                } while (cursor.moveToNext())
            }
        }
    } catch (e: Exception) {
        if (showError) {
            e.printStackTrace()
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

val Context.inflater: LayoutInflater get() = LayoutInflater.from(this)

val Context.isPortraitScreen get() = resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT