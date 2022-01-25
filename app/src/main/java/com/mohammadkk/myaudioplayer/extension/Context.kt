package com.mohammadkk.myaudioplayer.extension

import android.Manifest
import android.content.ContentUris
import android.content.Context
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.provider.MediaStore
import android.provider.MediaStore.Audio
import android.view.LayoutInflater
import androidx.core.content.ContextCompat
import androidx.loader.content.CursorLoader
import com.mohammadkk.myaudioplayer.helper.BuildUtil
import com.mohammadkk.myaudioplayer.helper.Constants
import com.mohammadkk.myaudioplayer.model.Albums
import com.mohammadkk.myaudioplayer.model.Songs
import java.util.*

fun Context.hasPermission(permission: String =  Manifest.permission.READ_EXTERNAL_STORAGE): Boolean {
    if (BuildUtil.isMarshmallowPlus()) {
        val base = ContextCompat.checkSelfPermission(this, permission)
        return base == PackageManager.PERMISSION_GRANTED
    }
    return true
}
fun Context.getAllAlbum(): ArrayList<Albums> {
    val mAlbums = ArrayList<Albums>()
    val uri = Audio.Albums.EXTERNAL_CONTENT_URI
    val projection = arrayOf(
        Audio.Albums._ID,
        Audio.Albums.ALBUM
    )
    queryCursor(uri, projection) { cursor ->
        val id = cursor.getLongVal(Audio.Albums._ID)
        val name = cursor.getStringVal(Audio.Albums.ALBUM)
        mAlbums.add(Albums(id, name))
    }
    return mAlbums
}

fun Context.getAllSongs(albumId: Long): ArrayList<Songs> {
    val mSongs = ArrayList<Songs>()
    val uri = if (BuildUtil.isQPlus()) {
        Audio.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
    } else Audio.Media.EXTERNAL_CONTENT_URI
    val projection = arrayOf(
        Audio.Media._ID,
        Audio.Media.DATA,
        Audio.Media.DURATION,
        Audio.Media.TITLE,
        Audio.Artists.ARTIST,
        Audio.Albums.ALBUM
    )
    val selection = "${Audio.Albums.ALBUM_ID} = ?"
    val selectionArgs = arrayOf(albumId.toString())
    queryCursor(uri, projection, selection, selectionArgs) { cursor ->
        val id = cursor.getLongVal(Audio.Media._ID)
        val path = cursor.getStringVal(Audio.Media.DATA)
        val duration = cursor.getIntVal(Audio.Media.DURATION) / 1000
        val albumArt = ContentUris.withAppendedId(Constants.ALBUM_ART, albumId).toString()
        val title = cursor.getStringVal(Audio.Media.TITLE)
        val artist = cursor.getStringOrNullVal(Audio.Artists.ARTIST) ?: ""
        val album = cursor.getStringOrNullVal(Audio.Albums.ALBUM) ?: ""
        mSongs.add(Songs(id, albumArt, duration, path, title, artist, album))
    }
    return mSongs
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
fun Context.getAlbumCoverByUri(uri: Uri): Bitmap? {
    var cover: Bitmap? = null
    try {
        val pfd = contentResolver.openFileDescriptor(uri, "r")
        pfd?.use {
            val fd = pfd.fileDescriptor
            cover = BitmapFactory.decodeFileDescriptor(fd)
        }
    } catch (e: java.lang.Exception) {
        e.printStackTrace()
    }
    return cover
}

val Context.inflater: LayoutInflater get() = LayoutInflater.from(this)