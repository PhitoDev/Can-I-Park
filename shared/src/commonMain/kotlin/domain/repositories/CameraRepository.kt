package domain.repositories

import domain.entities.ImageDetails

interface CameraRepository {
    fun startCamera(view: Any): Result<Unit>
    suspend fun takePicture(): Result<ImageDetails>
}