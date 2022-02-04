package com.mohammadkk.myaudioplayer.model

import java.io.Serializable

data class Track(
    val id: Long,
    val albumId: Long,
    val artistId: Long,
    val duration: Int,
    val path: String,
    val title: String,
    val displayName: String,
    val album: String,
    val artist: String,
) : Serializable {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as Track
        if (id != other.id) return false
        if (albumId != other.albumId) return false
        if (artistId != other.artistId) return false
        if (duration != other.duration) return false
        if (path != other.path) return false
        if (title != other.title) return false
        if (displayName != other.displayName) return false
        if (album != other.album) return false
        if (artist != other.artist) return false
        return true
    }
    override fun hashCode(): Int {
        var hash = id.hashCode()
        hash = 24 * hash + albumId.hashCode()
        hash = 24 * hash + artistId.hashCode()
        hash = 24 * hash + duration
        hash = 24 * hash + path.hashCode()
        hash = 24 * hash + title.hashCode()
        hash = 24 * hash + displayName.hashCode()
        hash = 24 * hash + album.hashCode()
        hash = 24 * hash + artist.hashCode()
        return hash
    }
    override fun toString(): String {
        return "Track(id: $id, albumId: $albumId, artistId: $artistId, " +
                "duration: $duration, path: $path, title: $title, displayName: $displayName, " +
                "album: $album, artist: $artist)"
    }
}