package com.mohammadkk.myaudioplayer.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.mohammadkk.myaudioplayer.helper.MusicUtil
import com.mohammadkk.myaudioplayer.model.Album
import kotlinx.coroutines.*

class AlbumViewModel(application: Application) : AndroidViewModel(application) {
    private val musicUtil = MusicUtil(application)
    private val viewModelJob = SupervisorJob()
    private val handleError = CoroutineExceptionHandler { _, e ->
        e.printStackTrace()
        deviceAlbum.value = null
    }
    private val uiDispatcher = Dispatchers.Main
    private val ioDispatcher = Dispatchers.IO + viewModelJob + handleError
    private val uiScope = CoroutineScope(uiDispatcher)

    val deviceAlbum = MutableLiveData<ArrayList<Album>?>()

    fun scanAlbumInDevice() {
        uiScope.launch {
            withContext(ioDispatcher) {
                val albums = musicUtil.fetchAllAlbum()
                withContext(uiDispatcher) {
                    deviceAlbum.value = albums
                }
            }
        }
    }
    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}