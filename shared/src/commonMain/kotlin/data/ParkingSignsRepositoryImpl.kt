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
        Json.decodeFromString(llmResponse)
    } catch (e: Exception) {
        ParkingResponse(
            canIPark = false,
            howLong = null,
            cost = null,
            reasonIfNo = e.message
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
            Tell me if I can park here right now. It is ${getCurrentSystemTime()},
            and the accompanying text from the parking signs say: $ocrResponse. 
            If I can park, how long can I park? If there is a cost, how much does it cost?
            If I can't park, why not?
            Please respond in the following JSON format based on these examples:
            {
                "canIPark": true,
                "howLong": "2 hours",
                "cost": "$2.00 per hour",
                "reasonIfNo": null
            }
            {
                "canIPark": false,
                "howLong": null,
                "cost": null,
                "reasonIfNo": "No parking on Sundays"
            }
        """.trimIndent()
    }
}