package data

interface LLMClient {
    suspend fun generateResponse(
        prompt: String,
        encodedBitmap: String? = null,
        rotationDegrees: Int = 0
    ): String
}