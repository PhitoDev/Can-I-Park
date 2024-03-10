package domain.entities

data class ParkingRequest(
    val image: ByteArray,
    val width: Int,
    val height: Int,
    val rotationDegrees: Int,
    val format: Int
)

data class BitmapRequest(
    val encodedBitmap: String,
    val rotationDegrees: Int
)