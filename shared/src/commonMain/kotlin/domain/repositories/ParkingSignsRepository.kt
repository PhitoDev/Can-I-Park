package domain.repositories

import domain.entities.ParkingRequest
import domain.entities.ParkingResponse

interface ParkingSignsRepository {
    suspend fun analyzeParkingSigns(request: ParkingRequest): Result<ParkingResponse>
}