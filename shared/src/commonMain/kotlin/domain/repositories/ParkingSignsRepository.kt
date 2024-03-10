package domain.repositories

import domain.entities.BitmapRequest
import domain.entities.ParkingRequest
import domain.entities.ParkingResponse

interface ParkingSignsRepository {
    suspend fun analyzeParkingSigns(request: ParkingRequest): Result<ParkingResponse>

    suspend fun analyzeParkingSigns(request: BitmapRequest): Result<ParkingResponse>
}