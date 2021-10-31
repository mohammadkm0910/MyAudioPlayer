package com.mohammadkk.myaudioplayer.fragment

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
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
    protected fun compareTo(list: ArrayList<Songs>) {
        list.sortWith { o1, o2 -> o1.title.compareTo(o2.title) }
    }
    protected fun isPortraitScreen(): Boolean {
        return requireActivity().resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT
    }
}