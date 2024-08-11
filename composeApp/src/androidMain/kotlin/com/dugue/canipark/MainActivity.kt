package com.dugue.canipark

import android.Manifest.permission.CAMERA
import android.content.ContentValues.TAG
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.getValue
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import co.touchlab.kermit.Logger
import com.dugue.canipark.ui.camera.CameraScreen
import com.dugue.canipark.ui.camera.CameraViewModel
import com.dugue.canipark.ui.camera.DisclaimerChecked
import com.google.android.gms.ads.MobileAds
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : ComponentActivity() {

    private val REQUIRED_PERMISSIONS = arrayOf(CAMERA)

    private val viewModel: CameraViewModel by viewModel()

    private val activityResultLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions())
        { permissions ->
            // Handle Permission granted/rejected
            if (permissions[CAMERA] == false) {
                Toast.makeText(baseContext, "Camera permission denied", Toast.LENGTH_SHORT).show()
            } else {
                Logger.i("$TAG Camera permission granted") // More explicit logging
            }
        }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestCameraPermission()
        setContent {
            val state by viewModel.uiState.collectAsStateWithLifecycle()
            viewModel.onEvent(DisclaimerChecked)
            MaterialTheme {
                CameraScreen(
                    appState = state.appState,
                    onEvent = viewModel::onEvent
                )
            }
        }
        MobileAds.initialize(this) {
            Logger.i("$TAG AdMob initialized")

        }
    }

    private fun requestCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, CAMERA) == PackageManager.PERMISSION_GRANTED) {
            Logger.i("$TAG Camera permission already granted")
        } else {
            activityResultLauncher.launch(REQUIRED_PERMISSIONS)
        }
    }

}

