package com.mohammadkk.myaudioplayer.helper

import android.content.Context
import android.provider.MediaStore
import com.mohammadkk.myaudioplayer.extension.*
import com.mohammadkk.myaudioplayer.model.Album
import com.mohammadkk.myaudioplayer.model.Artist
import com.mohammadkk.myaudioplayer.model.Track

class MusicUtil(private val context: Context) {
    fun fetchAllTracks(): ArrayList<Track> {
        val trackList = arrayListOf<Track>()
        val uri = if (BuildUtil.isQPlus()) {
            MediaStore.Audio.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
        } else MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
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
            val id = cursor.getLongVal(projection[0])
            val albumId = cursor.getLongVal(projection[1])
            val artistId = cursor.getLongVal(projection[2])
            val duration = cursor.getIntVal(projection[3]) / 1000
            val path = cursor.getStringVal(projection[4])
            val title = cursor.getStringVal(projection[5])
            val displayName = cursor.getStringVal(projection[6])
            val albums = cursor.getStringOrNullVal(projection[7]) ?: ""
            val artist = cursor.getStringOrNullVal(projection[8]) ?: ""
            val track = Track(id, albumId, artistId, duration, path, title, displayName, albums, artist)
            trackList.add(track)
        }
        return trackList
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
            val id = cursor.getLongVal(projection[0])
            val name = cursor.getStringOrNullVal(projection[1]) ?: ""
            val trackCount = cursor.getIntVal(projection[2])
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
            val id = cursor.getLongVal(projection[0])
            val name = cursor.getStringOrNullVal(projection[1]) ?: ""
            val trackCount = cursor.getIntVal(projection[2])
            val artist = Artist(id, name, trackCount)
            artistList.add(artist)
        }
        return artistList
    }
    fun fetchTracksByAlbumId(albumId: Long): ArrayList<Track> {
        val trackList = arrayListOf<Track>()
        fetchAllTracks().filter { it.albumId == albumId }.forEach { track ->
            trackList.add(track)
        }
        return trackList
    }
    fun fetchTracksByArtistId(artistId: Long): ArrayList<Track> {
        val trackList = arrayListOf<Track>()
        fetchAllTracks().filter { it.artistId == artistId }.forEach { track ->
            trackList.add(track)
        }
        return trackList
    }
    companion object {
        fun newInstance(context: Context) = MusicUtil(context)
    }
}