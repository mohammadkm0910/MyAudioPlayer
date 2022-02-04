package com.mohammadkk.myaudioplayer

import android.content.Context
import androidx.preference.PreferenceManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.mohammadkk.myaudioplayer.model.Track

class CacheApp(context: Context) {
    private val prefs = PreferenceManager.getDefaultSharedPreferences(context)

    fun storeTracks(tracks: ArrayList<Track>) {
        val editor = prefs.edit()
        val gson = Gson()
        val json = gson.toJson(tracks)
        editor.putString("track_array_list", json)
        editor.apply()
    }
    fun getStoreTracks(): ArrayList<Track>? {
        val gson = Gson()
        val json = prefs.getString("track_array_list", null)
        val type = object : TypeToken<ArrayList<Track>>() {}.type
        return gson.fromJson(json, type)
    }
    var iconPausedCaller: Int
        get() = prefs.getInt("icon_paused_caller_pref", R.drawable.ic_pause)
        set(value) = prefs.edit().putInt("icon_paused_caller_pref", value).apply()
    var titleTextCaller: String
        get() = prefs.getString("title_text_caller_pref", "") ?: ""
        set(value) = prefs.edit().putString("title_text_caller_pref", value).apply()
    var artistTextCaller: String
        get() = prefs.getString("artist_text_caller_pref", "") ?: ""
        set(value) = prefs.edit().putString("artist_text_caller_pref", value).apply()
    var globalTrackIndexCaller: Int
        get() = prefs.getInt("global_track_index_pref", -1)
        set(value) = prefs.edit().putInt("global_track_index_pref", value).apply()

    fun requirClear() {
        val editor = prefs.edit()
        editor.remove("track_array_list")
        editor.remove("icon_paused_caller_pref")
        editor.remove("title_text_caller_pref")
        editor.remove("artist_text_caller_pref")
        editor.remove("global_track_index_pref")
        if (editor.commit()) editor.apply()
    }
}