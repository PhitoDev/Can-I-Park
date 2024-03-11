package data

import android.graphics.BitmapFactory
import android.graphics.ImageFormat
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognizer
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

class AndroidOcrClient(private val recognizer: TextRecognizer) : OcrClient {

    override suspend fun analyzeParkingSigns(
        image: ByteArray,
        width: Int,
        height: Int,
        rotationDegrees: Int,
        format: Int
    ): String = suspendCoroutine { continuation ->
        val inputImage = toInputImage(image, width, height, rotationDegrees, format)
        recognizer.process(inputImage)
            .addOnSuccessListener { result ->
                val text = result.textBlocks.joinToString(" ") { it.text.replace("\n", " ") }
                continuation.resume(text)
            }
            .addOnFailureListener { e ->
                continuation.resumeWithException(e)
            }
    }

    @OptIn(ExperimentalEncodingApi::class)
    override suspend fun analyzeParkingSigns(
        encodedBitmap: String,
        rotationDegrees: Int
    ): String = suspendCoroutine {
        val byteArray = Base64.decode(encodedBitmap)
        val bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
        val inputImage = InputImage.fromBitmap(bitmap, rotationDegrees)
        recognizer.process(inputImage)
            .addOnSuccessListener { result ->
                val text = result.textBlocks.joinToString(" ") { it.text.replace("\n", " " ) }
                it.resume(text)
            }
            .addOnFailureListener { e ->
                it.resumeWithException(e)
            }
    }

    private fun toInputImage
                (image: ByteArray,
                 width: Int,
                 height: Int,
                 rotationDegrees: Int,
                 format: Int
    ): InputImage {
        return when (format) {
            ImageFormat.YUV_420_888 -> {
                val image = InputImage.fromByteArray(
                    image,
                    width,
                    height,
                    rotationDegrees,
                    InputImage.IMAGE_FORMAT_YUV_420_888
                )
                image
            }
            ImageFormat.NV21 -> {
                InputImage.fromByteArray(
                    image,
                    width,
                    height,
                    rotationDegrees,
                    InputImage.IMAGE_FORMAT_NV21
                )
            }
            else -> throw IllegalArgumentException("Unsupported image format")
        }
    }
}