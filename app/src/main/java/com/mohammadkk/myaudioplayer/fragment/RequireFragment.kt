package com.mohammadkk.myaudioplayer.fragment

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import com.mohammadkk.myaudioplayer.model.Albums
import com.mohammadkk.myaudioplayer.model.Songs

open class RequireFragment : Fragment() {
    protected fun runTimePermission(callback:()->Unit) {
        if (!isGrantedPermission()) {
            registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
                val permissionsSet = arrayListOf<Boolean>()
                permissions.entries.forEach {
                    permissionsSet.add(it.value)
                }
                if (permissionsSet[0] || permissionsSet[1]) {
                    callback()
                } else {
                    Snackbar.make(requireView(), "Permission Deny", Snackbar.LENGTH_SHORT)
                        .setAction("Settings") {
                            Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                                data = Uri.fromParts("package", requireContext().packageName, null)
                                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                startActivity(this)
                            }
                        }.show()
                }
            }.launch(
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            )
        } else callback()
    }

    private fun isGrantedPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val read = ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
            val write = ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
            read == PackageManager.PERMISSION_GRANTED || write == PackageManager.PERMISSION_GRANTED
        } else true
    }
    protected fun compareSongs(list: ArrayList<Songs>) {
        list.sortWith { o1, o2 -> o1.title.compareTo(o2.title) }
    }
    protected fun compareAlbums(list: ArrayList<Albums>) {
        list.sortWith { o1, o2 -> o1.name.compareTo(o2.name) }
    }
    protected fun isPortraitScreen(): Boolean {
        return requireActivity().resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT
    }
}