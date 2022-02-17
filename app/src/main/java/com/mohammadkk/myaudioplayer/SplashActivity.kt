package com.mohammadkk.myaudioplayer

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.motion.widget.MotionLayout
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.mohammadkk.myaudioplayer.databinding.ActivitySplashBinding
import com.mohammadkk.myaudioplayer.extension.hasPermission
import com.mohammadkk.myaudioplayer.extension.showToast

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {
    private lateinit var runActivity: ActivityResultLauncher<Intent>
    private lateinit var resultPermission: ActivityResultLauncher<Array<String>>
    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)
        runActivity = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (hasPermission()) {
                startActivity(Intent(this@SplashActivity, MainActivity::class.java))
                finish()
            }
        }
        resultPermission = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions->
            val results = arrayListOf<Boolean>()
            permissions.entries.forEach { results.add(it.value) }
            if (results[0] || results[1]) {
                startApp()
            } else {
                MaterialAlertDialogBuilder(this).setTitle(R.string.app_name)
                    .setMessage(R.string.permission_message)
                    .setCancelable(false)
                    .setPositiveButton(android.R.string.ok) { dialog, _ ->
                        Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                            data = Uri.fromParts("package", BuildConfig.APPLICATION_ID, null)
                            runActivity.launch(this)
                        }
                        dialog.dismiss()
                    }
                    .setNegativeButton(android.R.string.cancel) { dialog, _ ->
                        showToast(getString(R.string.permission_deny))
                        moveTaskToBack(true)
                        dialog.dismiss()
                    }.create().show()
            }
        }
        runApp()
    }
    private fun runApp() {
        binding.root.addTransitionListener(object : MotionLayout.TransitionListener {
            override fun onTransitionStarted(motionLayout: MotionLayout?, startId: Int, endId: Int) {}
            override fun onTransitionChange(motionLayout: MotionLayout?, startId: Int, endId: Int, progress: Float) {}
            override fun onTransitionCompleted(motionLayout: MotionLayout?, currentId: Int) {
                if (hasPermission()) {
                    startApp()
                } else {
                    resultPermission.launch(
                        arrayOf(
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                        )
                    )
                }
            }
            override fun onTransitionTrigger(motionLayout: MotionLayout?, triggerId: Int, positive: Boolean, progress: Float) {}
        })
    }
    private fun startApp() {
        startActivity(Intent(this@SplashActivity, MainActivity::class.java))
        finish()
    }
}