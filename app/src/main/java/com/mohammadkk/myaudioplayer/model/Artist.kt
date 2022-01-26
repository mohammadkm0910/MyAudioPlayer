package com.mohammadkk.myaudioplayer.model

data class Artist(val id: Long, val name: String, val trackCount: Int) {
    override fun toString(): String {
        return "Artist(id: $id, name: $name, trackCount: $trackCount)"
    }
}