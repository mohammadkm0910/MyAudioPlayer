package com.mohammadkk.myaudioplayer.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Songs(
    val id: Long,
    val albumArt: String,
    val duration: Int,
    val path: String,
    val title: String,
    val artist: String,
    val album: String
) : Parcelable {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as Songs
        if (id != other.id) return false
        if (albumArt != other.albumArt) return false
        if (duration != other.duration) return false
        if (path != other.path) return false
        if (title != other.title) return false
        if (artist != other.artist) return false
        if (album != other.album) return false
        return true
    }
    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + albumArt.hashCode()
        result = 31 * result + duration
        result = 31 * result + path.hashCode()
        result = 31 * result + title.hashCode()
        result = 31 * result + artist.hashCode()
        result = 31 * result + album.hashCode()
        return result
    }
    override fun toString(): String {
        return "Songs(id: $id, albumArt: $albumArt, duration: $duration," +
                " path: $path, title: $title, artist: $artist, album: $album)"
    }
}