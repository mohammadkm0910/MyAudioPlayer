package com.mohammadkk.myaudioplayer.helper

import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import com.mohammadkk.myaudioplayer.extension.*
import com.mohammadkk.myaudioplayer.model.Album
import com.mohammadkk.myaudioplayer.model.Artist
import com.mohammadkk.myaudioplayer.model.Track

class MusicUtil(private val context: Context) {
    fun fetchAllTracks(): ArrayList<Track> {
        val tracks = arrayListOf<Track>()
        val uri = EXTERNAL_TRACK_URI
        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.ALBUM_ID,
            MediaStore.Audio.Media.ARTIST_ID,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.DISPLAY_NAME,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.ARTIST
        )
        context.queryCursor(uri, projection) { cursor ->
            val id = cursor.getLongVal(MediaStore.Audio.Media._ID)
            val albumId = cursor.getLongVal(MediaStore.Audio.Media.ALBUM_ID)
            val artistId = cursor.getLongVal(MediaStore.Audio.Media.ARTIST_ID)
            val duration = cursor.getIntVal(MediaStore.Audio.Media.DURATION) / 1000
            val path = cursor.getStringVal(MediaStore.Audio.Media.DATA)
            val title = cursor.getStringVal(MediaStore.Audio.Media.TITLE)
            val displayName = cursor.getStringVal(MediaStore.Audio.Media.DISPLAY_NAME)
            val album = cursor.getStringOrNullVal(MediaStore.Audio.Media.ALBUM) ?: ""
            val artist = cursor.getStringOrNullVal(MediaStore.Audio.Media.ARTIST) ?: ""
            val track = Track(id, albumId, artistId, duration, path, title, displayName, album, artist)
            tracks.add(track)
        }
        return tracks
    }
    fun fetchAllAlbum(): ArrayList<Album> {
        val albumList = arrayListOf<Album>()
        val uri = MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI
        val projection = arrayOf(
            MediaStore.Audio.Albums._ID,
            MediaStore.Audio.Albums.ALBUM,
            MediaStore.Audio.Albums.NUMBER_OF_SONGS
        )
        context.queryCursor(uri, projection) { cursor ->
            val id = cursor.getLongVal(MediaStore.Audio.Albums._ID)
            val name = cursor.getStringOrNullVal(MediaStore.Audio.Albums.ALBUM) ?: ""
            val trackCount = cursor.getIntVal(MediaStore.Audio.Albums.NUMBER_OF_SONGS)
            val album = Album(id, name, trackCount)
            albumList.add(album)
        }
        return albumList
    }
    fun fetchAllArtist(): ArrayList<Artist> {
        val artistList = arrayListOf<Artist>()
        val uri = MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI
        val projection = arrayOf(
            MediaStore.Audio.Artists._ID,
            MediaStore.Audio.Artists.ARTIST,
            MediaStore.Audio.Artists.NUMBER_OF_TRACKS
        )
        context.queryCursor(uri, projection) { cursor ->
            val id = cursor.getLongVal(MediaStore.Audio.Artists._ID)
            val name = cursor.getStringOrNullVal(MediaStore.Audio.Artists.ARTIST) ?: ""
            val trackCount = cursor.getIntVal(MediaStore.Audio.Artists.NUMBER_OF_TRACKS)
            val artist = Artist(id, name, trackCount)
            artistList.add(artist)
        }
        return artistList
    }
    companion object {
        val EXTERNAL_TRACK_URI: Uri = if (BuildUtil.isQPlus()) {
            MediaStore.Audio.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
        } else MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
    }
}