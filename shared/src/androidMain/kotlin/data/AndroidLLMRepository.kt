package data

import Utils.getCurrentSystemTime
import android.graphics.BitmapFactory
import co.touchlab.kermit.Logger
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.Content
import domain.entities.ImageDetails
import domain.entities.ParkingResponse
import domain.repositories.LLMRepository
import kotlinx.serialization.json.Json
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

class AndroidLLMRepository(private val generativeModel: GenerativeModel) : LLMRepository {

    override suspend fun analyzeImage(request: ImageDetails): Result<ParkingResponse> =
        runCatching {
            val llmResponse = generateResponse(
                prompt = formatPrompt(),
                imageDetails = request
            )
            parkingResponseFromJson(llmResponse)
        }

    private fun parkingResponseFromJson(llmResponse: String): ParkingResponse =
        try {
            val json = llmResponse
                .replace("```json\n", "")
                .replace("```", "")
                .replace("```json", "")
            Json.decodeFromString<ParkingResponse>(json)
        } catch (e: Exception) {
            val message = "Failed to parse llm response: $llmResponse \n" + e.message.toString()
            Logger.e { message }
            throw Exception(message)
        }



    @OptIn(ExperimentalEncodingApi::class)
    private suspend fun generateResponse(prompt: String, imageDetails: ImageDetails): String {
        val builder = Content.Builder()
        val  byteArray = Base64.decode(imageDetails.encodedBitmap)
        val bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
        builder.image(bitmap)
        builder.text(prompt)
        val response = generativeModel.generateContent(builder.build())
        return response.text ?: throw IllegalStateException("Response is null")
    }

    /**
     * This function takes  The text from an ocr analyzer and formats it into a prompt for the language model.
     * The prompt requests the language model to generate a response in the format of a ParkingResponse.
     * @param ocrResponse The text from the ocr analyzer.
     * @return A prompt for the language model.
     */
    private fun formatPrompt(): String {
        return """
            It is currently ${getCurrentSystemTime()}. Tell me if I can park here right now based on 
            the current time and the image provided, if it is a valid image of parking signs.
            If I can park, how long can I park? If there is no time limit, this field should be null.
            If there is a cost, how much does it cost? If no cost, this field should be null.
            Are there any restrictions? If there are no restrictions, this field should be null.
            If I can't park, why not? If I can park, this field should be null.
            Please only respond in JSON format based on the following schema and examples:
            {
                "title": "Parking Response",
                "type": "object",
                "properties": {
                    "canIPark": {
                        "type": "boolean",
                        "description": "Whether or not the user can park at the location"
                    },
                    "howLong": {
                        "type": "string",
                        "description": "How long the user can park at the location"
                    },
                    "cost": {
                        "type": "string",
                        "description": "The cost of parking at the location"
                    },
                    "reasonIfNo": {
                        "type": "string",
                        "description": "The reason the user cannot park at the location"
                    },
                    "restrictions": {
                        "type": "string",
                        "description": "The restrictions on parking at the location"
                    }
                },
                "required": ["canIPark"]
            },
            {
                "canIPark": true,
                "howLong": "2 hours",
                "cost": "$2.00 per hour",
                "reasonIfNo": null,
                "restrictions": Only cars with permits
            },
            {
                "canIPark": false,
                "howLong": null,
                "cost": null,
                "reasonIfNo": "No parking on Sundays",
                "restrictions": null
            }.
            If the image provided is not a valid image of parking signs,
            respond in the format with the "reasonIfNo" being "This is not a valid image for analysis".
            Do not include the schema in your response.
        """.trimIndent()
    }
}