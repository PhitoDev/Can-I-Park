package com.dugue.canipark

import android.Manifest.permission.CAMERA
import android.content.ContentValues.TAG
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.getValue
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import co.touchlab.kermit.Logger
import com.dugue.canipark.ui.camera.CameraEvent
import com.dugue.canipark.ui.camera.CameraScreen
import com.dugue.canipark.ui.camera.CameraViewModel
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import domain.entities.ImageDetails
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.ByteArrayOutputStream
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

class MainActivity : ComponentActivity() {

    private val REQUIRED_PERMISSIONS = arrayOf(CAMERA)

    private val viewModel: CameraViewModel by viewModel()

    private lateinit var imageCapture: ImageCapture

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
            viewModel.onEvent(CameraEvent.DisclaimerChecked)
            MaterialTheme {
                CameraScreen(
                    cameraState = state.cameraState,
                    onCameraReady = { view ->
                        startCamera(view)
                                    },
                    onPictureTaken = { takePhoto() },
                    onDismiss = { viewModel.onEvent(CameraEvent.MessageDismissed) },
                    onAdViewReady = { view -> setupBannerAd(view) }
                )
            }
        }
        MobileAds.initialize(this) {
            Logger.i("$TAG AdMob initialized")

        }
    }

    private fun startCamera(view: PreviewView) {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            // Used to bind the lifecycle of cameras to the lifecycle owner
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            // Preview
            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(view.surfaceProvider)
                }

            imageCapture = ImageCapture.Builder()
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MAXIMIZE_QUALITY)
                .setTargetRotation(view.display.rotation)
                .build()

            // Select back camera as a default
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                // Unbind use cases before rebinding
                cameraProvider.unbindAll()

                // Bind use cases to camera
                cameraProvider.bindToLifecycle(
                    this, cameraSelector, imageCapture, preview)

            } catch(exc: Exception) {
                Logger.e("$TAG Use case binding failed", exc)
            }

        }, ContextCompat.getMainExecutor(this))
    }

    private fun requestCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, CAMERA) == PackageManager.PERMISSION_GRANTED) {
            Logger.i("$TAG Camera permission already granted")
        } else {
            activityResultLauncher.launch(REQUIRED_PERMISSIONS)
        }
    }

    private fun takePhoto() {
        imageCapture.takePicture(
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageCapturedCallback() {
                override fun onCaptureSuccess(image: ImageProxy) {
                    val rotationDegrees = image.imageInfo.rotationDegrees
                    val imageDetails = ImageDetails(
                        encodedBitmap = encodeBitmapToString(image.toBitmap()),
                        rotationDegrees = rotationDegrees
                    )
                    viewModel.onEvent(CameraEvent.PictureTaken(imageDetails))
                    image.close()
                }

                override fun onError(exception: ImageCaptureException) {
                    Log.e(TAG, "Photo capture failed: ${exception.message}", exception)
                }
            }
        )
    }

    @OptIn(ExperimentalEncodingApi::class)
    private fun encodeBitmapToString(bitmap: Bitmap): String {
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
        return Base64.encode(stream.toByteArray())
    }

    private fun setupBannerAd(view: AdView) {
        view.adListener = object : AdListener() {
            override fun onAdLoaded() {
                Logger.i("$TAG Ad loaded")
            }

            override fun onAdFailedToLoad(error: LoadAdError) {
                Logger.e("$TAG Ad failed to load: ${error.message}")
            }
        }
    }
}

