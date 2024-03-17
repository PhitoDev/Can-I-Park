package domain.entities

data class ParkingRequest(
    val encodedBitmap: String,
    val rotationDegrees: Int
)