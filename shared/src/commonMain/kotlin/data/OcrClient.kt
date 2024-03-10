package data

interface OcrClient {
    suspend fun analyzeParkingSigns(
        image: ByteArray,
        width: Int,
        height: Int,
        rotationDegrees: Int,
        format: Int
    ): String

    suspend fun analyzeParkingSigns(encodedBitmap:String, rotationDegrees: Int): String
}