package domain.entities

import kotlinx.serialization.Serializable

@Serializable
data class ParkingResponse(
    val canIPark: Boolean,
    val howLong: String? = null,
    val cost: String? = null,
    val reasonIfNo: String? = null,
    val restrictions: String? = null
)
