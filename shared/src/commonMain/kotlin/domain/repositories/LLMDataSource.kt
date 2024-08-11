package domain.repositories

import domain.entities.ImageDetails

interface LLMDataSource {
    suspend fun generateResponse(
        prompt: String,
        imageDetails: ImageDetails
    ): String
}