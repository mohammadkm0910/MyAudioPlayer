package com.mohammadkk.myaudioplayer.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.mohammadkk.myaudioplayer.helper.MusicUtil
import com.mohammadkk.myaudioplayer.model.Artist
import kotlinx.coroutines.*

class ArtistViewModel(application: Application) : AndroidViewModel(application) {
    private val musicUtil = MusicUtil(application)
    private val viewModelJob = SupervisorJob()
    private val handleError = CoroutineExceptionHandler { _, e ->
        e.printStackTrace()
        deviceArtist.value = null
    }
    private val uiDispatcher = Dispatchers.Main
    private val ioDispatcher = Dispatchers.IO + viewModelJob + handleError
    private val uiScope = CoroutineScope(uiDispatcher)

    val deviceArtist = MutableLiveData<ArrayList<Artist>?>()

    fun scanArtistInDevice() {
        uiScope.launch {
            withContext(ioDispatcher) {
                val artists = musicUtil.fetchAllArtist()
                withContext(uiDispatcher) {
                    deviceArtist.value = artists
                }
            }
        }
    }
    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}