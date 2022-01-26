package com.mohammadkk.myaudioplayer.model

data class Album(val id: Long, val name: String, val trackCount: Int) {
    override fun toString(): String {
        return "Album(id: $id, name: $name, trackCount: $trackCount)"
    }
}