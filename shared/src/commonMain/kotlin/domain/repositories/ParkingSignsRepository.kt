package domain.repositories

import domain.entities.ImageDetails
import domain.entities.ParkingResponse

interface ParkingSignsRepository {
    suspend fun analyzeImage(request: ImageDetails): Result<ParkingResponse>
}