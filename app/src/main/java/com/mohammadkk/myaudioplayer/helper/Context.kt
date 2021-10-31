package com.mohammadkk.myaudioplayer.helper

import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.net.Uri
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
        val id = cursor.getLong(cursor.getColumnIndexOrThrow(Audio.Albums._ID))
        val name = cursor.getString(cursor.getColumnIndexOrThrow(Audio.Albums.ALBUM))
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
        Audio.Media.TITLE,
        Audio.Artists.ARTIST,
        Audio.Albums.ALBUM
    )
    val selection = "${Audio.Albums.ALBUM_ID} = ?"
    val selectionArgs = arrayOf(albumId.toString())
    queryCursor(uri, projection, selection, selectionArgs) { cursor ->
            val id = cursor.getLong(cursor.getColumnIndexOrThrow(Audio.Media._ID))
            val path = cursor.getString(cursor.getColumnIndexOrThrow(Audio.Media.DATA))
            val albumArt = ContentUris.withAppendedId(MusicUtil.ALBUM_ART, albumId).toString()
            val title = cursor.getString(cursor.getColumnIndexOrThrow(Audio.Media.TITLE))
            val artist = cursor.getString(cursor.getColumnIndexOrThrow(Audio.Artists.ARTIST))
            val album = cursor.getString(cursor.getColumnIndexOrThrow(Audio.Albums.ALBUM))
            mSongs.add(Songs(id, albumArt, path, title, artist, album))
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
