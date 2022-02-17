package com.mohammadkk.myaudioplayer.service

import android.app.Service
import android.content.Intent
import android.os.*
import android.provider.MediaStore
import android.webkit.MimeTypeMap
import com.mohammadkk.myaudioplayer.extension.getInternalStorage
import com.mohammadkk.myaudioplayer.extension.getStringVal
import com.mohammadkk.myaudioplayer.extension.queryCursor
import com.mohammadkk.myaudioplayer.helper.MusicUtil
import java.io.File

class ScanLibraryService : Service() {
    private val localBinder: IBinder = LocalService()

    override fun onBind(intent: Intent?): IBinder {
        return localBinder
    }
    fun scanRequireLibrary(callback: (library: ArrayList<File>, filesNot: ArrayList<File>) -> Unit) {
        val handlerThread = HandlerThread("scan_library")
        handlerThread.start()
        val looper = handlerThread.looper
        Handler(looper).post {
            val fileNotExists = getMediaNotExists()
            val tracks = getLibraryScanDevice(getInternalStorage())
            Handler(Looper.getMainLooper()).post {
                callback(tracks, fileNotExists)
                handlerThread.quit()
            }
        }
    }
    private fun getLibraryScanDevice(storage: File): ArrayList<File> {
        val listFiles = arrayListOf<File>()
        val files = storage.listFiles() ?: return arrayListOf()
        for (singleFile in files) {
            if (singleFile.isDirectory && !singleFile.isHidden) {
                listFiles.addAll(getLibraryScanDevice(singleFile))
            } else if (isAudioSlow(singleFile.name)) {
                listFiles.add(singleFile)
            }
        }
        return listFiles
    }
    private fun getMediaNotExists(): ArrayList<File> {
        val listFiles = arrayListOf<File>()
        val uri = MusicUtil.EXTERNAL_TRACK_URI
        val projection = arrayOf(MediaStore.Audio.Media.DATA)
        queryCursor(uri, projection) { cursor ->
            val path = cursor.getStringVal(MediaStore.Audio.Media.DATA)
            val file = File(path)
            if (!file.exists()) listFiles.add(file)
        }
        return listFiles
    }
    private fun getMimetype(text: String): String {
        val extension = text.substringAfterLast('.')
        return MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension) ?: ""
    }
    private fun getAudioExtension(): Array<String> {
        return arrayOf(".mp3", ".wav", ".wma", ".ogg", ".m4a", ".opus", ".flac", ".aac")
    }
    private fun isAudioSlow(path: String): Boolean {
        val isFastTypeAudio = getAudioExtension().any { path.endsWith(it, true) }
        return isFastTypeAudio || getMimetype(path).startsWith("audio")
    }
    inner class LocalService: Binder() {
        val service: ScanLibraryService get() = this@ScanLibraryService
    }
}