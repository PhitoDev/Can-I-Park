package data

import Utils.getCurrentSystemTime
import co.touchlab.kermit.Logger
import domain.entities.ParkingRequest
import domain.entities.ParkingResponse
import domain.repositories.ParkingSignsRepository
import kotlinx.serialization.json.Json

class ParkingSignsRepositoryImpl(
    private val llmClient: LLMClient,
    private val ocrClient: OcrClient
): ParkingSignsRepository {

    override suspend fun analyzeParkingSigns(request: ParkingRequest): Result<ParkingResponse> =
        try {
            val ocrResponse = ocrClient.analyzeParkingSigns(request.encodedBitmap, request.rotationDegrees)
            val formattedPrompt = formatPrompt(ocrResponse)
            val llmResponse = llmClient.generateResponse(
                prompt = formattedPrompt,
                encodedBitmap = request.encodedBitmap,
                rotationDegrees = request.rotationDegrees
            )
            val parkingResponse = parkingResponseFromJson(llmResponse)
            Result.success(parkingResponse)
        } catch (e: Exception) {
            Logger.e { e.message.toString() }
            Result.failure(e)
        }

    private fun parkingResponseFromJson(llmResponse: String): ParkingResponse = try {
        val json = llmResponse
            .replace("```json\n", "")
            .replace("```", "")
            .replace("```json", "")
        Json.decodeFromString(json)
    } catch (e: Exception) {
        ParkingResponse(
            canIPark = false,
            howLong = null,
            cost = null,
            reasonIfNo = e.message,
        )
    }

    /**
     * This function takes  The text from an ocr analyzer and formats it into a prompt for the language model.
     * The prompt requests the language model to generate a response in the format of a ParkingResponse.
     * @param ocrResponse The text from the ocr analyzer.
     * @return A prompt for the language model.
     */
    private fun formatPrompt(ocrResponse: String): String {
        return """
            It is currently ${getCurrentSystemTime()}, and the accompanying text from the provided 
            image is: `$ocrResponse`. Tell me if I can park here right now based on 
            that information and the image provided, if it is a valid image of parking signs.
            If I can park, how long can I park? 
            If there is a cost, how much does it cost?
            Are there any restrictions?
            If I can't park, why not? 
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