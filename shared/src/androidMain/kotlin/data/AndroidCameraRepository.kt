package data

import android.content.Context
import android.graphics.Bitmap
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCapture.OnImageCapturedCallback
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import domain.entities.ImageDetails
import domain.repositories.CameraRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.onFailure
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.first
import java.io.ByteArrayOutputStream
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

class AndroidCameraRepository(private val context: Context) : CameraRepository {
    private lateinit var imageCapture: ImageCapture

    override fun startCamera(view: Any): Result<Unit> =
        runCatching {
            with(view as PreviewView) {
                val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
                cameraProviderFuture.addListener({
                    val cameraProvider = cameraProviderFuture.get()
                    val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
                    val preview = Preview.Builder().build()
                    imageCapture = ImageCapture.Builder().build()
                    cameraProvider.bindToLifecycle(
                        context as androidx.lifecycle.LifecycleOwner,
                        cameraSelector,
                        preview,
                        imageCapture
                    )
                    preview.setSurfaceProvider(surfaceProvider)
                }, ContextCompat.getMainExecutor(context))
            }
        }

    override suspend fun takePicture(): Result<ImageDetails> =
        runCatching { flowFromImageCapture().first() }

    private fun flowFromImageCapture(): Flow<ImageDetails> =
        callbackFlow {
            val callback = object : OnImageCapturedCallback() {
                override fun onCaptureSuccess(image: ImageProxy) {
                    val rotationDegrees = image.imageInfo.rotationDegrees
                    val imageDetails = ImageDetails(
                        encodedBitmap = encodeBitmapToString(image.toBitmap()),
                        rotationDegrees = rotationDegrees
                    )
                    image.close()
                    trySendBlocking(imageDetails)
                        .onFailure { close(it) }
                }

                override fun onError(exception: ImageCaptureException) {
                    cancel(CancellationException("Photo capture failed: ${exception.message}", exception))
                }
            }
            imageCapture.takePicture(ContextCompat.getMainExecutor(context), callback)
        }

    @OptIn(ExperimentalEncodingApi::class)
    private fun encodeBitmapToString(bitmap: Bitmap): String {
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
        return Base64.encode(stream.toByteArray())
    }
}