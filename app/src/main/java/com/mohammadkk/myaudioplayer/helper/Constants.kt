package com.mohammadkk.myaudioplayer.helper

import android.app.PendingIntent
import android.net.Uri

object Constants {
    const val TAG_SONGS_FRAGMENT = "tag_songs_frag"
    const val TAG_ALBUMS_FRAGMENT = "tag_albums_frag"
    const val TAG_ARTISTS_FRAGMENT = "tag_artists_frag"

    val ALBUM_ART: Uri = Uri.parse("content://media/external/audio/albumart")

    const val EXTRA_PAGE_TYPE = "extra_page_type"
    const val EXTRA_PAGE_SELECTED_ID = "extra_page_selected_id"
    val PENDING_INTENT_FLAG = if (BuildUtil.isMarshmallowPlus()) {
        PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
    } else 0
}