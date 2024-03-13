package com.ralphdugue.canipark

import android.Manifest.permission.CAMERA
import android.content.ContentValues.TAG
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageFormat
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
import androidx.compose.runtime.getValue
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ralphdugue.canipark.ui.camera.CameraEvent
import com.ralphdugue.canipark.ui.camera.CameraScreen
import com.ralphdugue.canipark.ui.camera.CameraViewModel
import domain.entities.BitmapRequest
import domain.entities.ParkingRequest
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.ByteArrayOutputStream
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

class MainActivity : ComponentActivity() {

    private val REQUIRED_PERMISSIONS = arrayOf(
        android.Manifest.permission.CAMERA
    )

    private val viewModel: CameraViewModel by viewModel()

    private lateinit var imageCapture: ImageCapture

    private val activityResultLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions())
        { permissions ->
            // Handle Permission granted/rejected
            if (permissions[android.Manifest.permission.CAMERA] == false) {
                Toast.makeText(baseContext, "Camera permission denied", Toast.LENGTH_SHORT).show()
            } else {
                Log.d(TAG, "Camera permission granted") // More explicit logging
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestCameraPermission()
        setContent {
            val state by viewModel.uiState.collectAsStateWithLifecycle()
            CameraScreen(
                cameraState = state.cameraState,
                onCameraReady = { view -> startCamera(view) },
                onPictureTaken = { takePhoto() },
                onDismiss = { viewModel.onEvent(CameraEvent.ResultDismissed) }
            )
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
                Log.e(TAG, "Use case binding failed", exc)
            }

        }, ContextCompat.getMainExecutor(this))
    }

    private fun requestCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, CAMERA) == PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "Camera permission already granted")
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
                    if (image.format == ImageFormat.JPEG) {
                        val bitmapRequest = BitmapRequest(
                            encodedBitmap = encodeBitmapToString(image.toBitmap()),
                            rotationDegrees = rotationDegrees
                        )
                        viewModel.onEvent(CameraEvent.PictureTakenBitmap(bitmapRequest))
                    } else {
                        val buffer = image.planes[0].buffer
                        val byteArray = ByteArray(buffer.remaining())
                        val parkingRequest = ParkingRequest(
                            image = byteArray,
                            width = image.width,
                            height = image.height,
                            rotationDegrees = rotationDegrees,
                            format = image.format
                        )
                        viewModel.onEvent(CameraEvent.PictureTaken(parkingRequest))
                    }
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
}

