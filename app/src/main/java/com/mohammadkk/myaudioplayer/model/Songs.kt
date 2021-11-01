package com.mohammadkk.myaudioplayer.model

import android.os.Parcel
import android.os.Parcelable
import android.os.Parcelable.Creator

data class Songs(
    val id: Long,
    val albumArt: String,
    val duration: Int,
    val path: String,
    val title: String,
    val artist: String,
    val album: String
) : Parcelable {

    constructor(source: Parcel) : this(
        source.readLong(),
        source.readString() ?: "",
        source.readInt(),
        source.readString() ?: "",
        source.readString() ?: "",
        source.readString() ?: "",
        source.readString() ?: ""
    )
    override fun describeContents(): Int {
        return 0
    }
    override fun writeToParcel(dest: Parcel?, flags: Int) {
        dest?.writeLong(this.id)
        dest?.writeString(this.albumArt)
        dest?.writeInt(this.duration)
        dest?.writeString(this.path)
        dest?.writeString(this.title)
        dest?.writeString(this.artist)
        dest?.writeString(this.album)
    }
    companion object {
        @Suppress("unused")
        @JvmField
        val CREATOR = object : Creator<Songs> {
            override fun createFromParcel(source: Parcel?): Songs {
                return Songs(source!!)
            }
            override fun newArray(size: Int): Array<Songs?> {
                return arrayOfNulls(size)
            }
        }
    }
}