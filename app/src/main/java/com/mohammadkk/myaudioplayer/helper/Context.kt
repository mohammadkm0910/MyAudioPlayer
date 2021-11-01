package com.mohammadkk.myaudioplayer.helper

import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.ParcelFileDescriptor
import android.provider.MediaStore.Audio
import androidx.loader.content.CursorLoader
import com.mohammadkk.myaudioplayer.model.Albums
import com.mohammadkk.myaudioplayer.model.Songs
import java.util.*

fun Context.getAllAlbum(): ArrayList<Albums> {
    val mAlbums = ArrayList<Albums>()
    val uri = Audio.Albums.EXTERNAL_CONTENT_URI
    val projection = arrayOf(
        Audio.Albums._ID,
        Audio.Albums.ALBUM
    )
    queryCursor(uri, projection) { cursor ->
        val id = cursor.getLongValue(Audio.Albums._ID)
        val name = cursor.getStringValue(Audio.Albums.ALBUM)
        mAlbums.add(Albums(id, name))
    }
    return mAlbums
}
fun Context.getAllSongs(albumId: Long): ArrayList<Songs> {
    val mSongs = ArrayList<Songs>()
    val uri = Audio.Media.EXTERNAL_CONTENT_URI
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
            val id = cursor.getLongValue(Audio.Media._ID)
            val path = cursor.getStringValue(Audio.Media.DATA)
            val duration = cursor.getIntValue(Audio.Media.DURATION) / 1000
            val albumArt = ContentUris.withAppendedId(MusicUtil.ALBUM_ART, albumId).toString()
            val title = cursor.getStringValue(Audio.Media.TITLE)
            val artist = cursor.getStringValue(Audio.Artists.ARTIST)
            val album = cursor.getStringValue(Audio.Albums.ALBUM)
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
    val cursor = CursorLoader(this, uri, projection, selection, selectionArgs, sortOrder).loadInBackground()
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
