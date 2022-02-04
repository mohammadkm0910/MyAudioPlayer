package com.mohammadkk.myaudioplayer.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.mohammadkk.myaudioplayer.helper.MusicUtil
import com.mohammadkk.myaudioplayer.model.Track
import kotlinx.coroutines.*

class TrackViewModel(application: Application) : AndroidViewModel(application) {
    private val musicUtil = MusicUtil(application)
    private val viewModelJob = SupervisorJob()
    private val handleError = CoroutineExceptionHandler { _, e ->
        e.printStackTrace()
        deviceTrack.value = null
    }
    private val uiDispatcher = Dispatchers.Main
    private val ioDispatcher = Dispatchers.IO + viewModelJob + handleError
    private val uiScope = CoroutineScope(uiDispatcher)

    val deviceTrack = MutableLiveData<ArrayList<Track>?>()

    fun scanTrackInDevice() {
        uiScope.launch {
            withContext(ioDispatcher) {
                val tracks = musicUtil.fetchAllTracks()
                withContext(uiDispatcher) {
                    deviceTrack.value = tracks
                }
            }
        }
    }
    fun scanTrackInDeviceByAlbum(albumId: Long) {
        uiScope.launch {
            withContext(ioDispatcher) {
                val tracks = arrayListOf<Track>()
                musicUtil.fetchAllTracks().filter {
                    it.albumId == albumId
                }.forEach { tracks.add(it) }
                withContext(uiDispatcher) {
                    deviceTrack.value = tracks
                }
            }
        }
    }
    fun scanTrackInDeviceByArtist(artistId: Long) {
        uiScope.launch {
            withContext(ioDispatcher) {
                val tracks = arrayListOf<Track>()
                musicUtil.fetchAllTracks().filter {
                    it.artistId == artistId
                }.forEach { tracks.add(it) }
                withContext(uiDispatcher) {
                    deviceTrack.value = tracks
                }
            }
        }
    }
    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
    fun cancel() {
        onCleared()
    }
}