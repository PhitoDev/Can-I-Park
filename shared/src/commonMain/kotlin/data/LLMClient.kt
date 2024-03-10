package data

interface LLMClient {
    suspend fun generateResponse(prompt: String): String
}