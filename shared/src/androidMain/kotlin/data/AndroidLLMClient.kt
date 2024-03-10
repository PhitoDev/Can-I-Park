package data

import com.google.ai.client.generativeai.GenerativeModel

class AndroidLLMClient(private val generativeModel: GenerativeModel) : LLMClient {

    override suspend fun generateResponse(prompt: String): String {
        val response = generativeModel.generateContent(prompt)
        return response.text ?: throw IllegalStateException("Response is null")
    }
}