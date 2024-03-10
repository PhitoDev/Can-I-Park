package data

import domain.entities.BitmapRequest
import domain.entities.ParkingRequest
import domain.entities.ParkingResponse
import domain.repositories.ParkingSignsRepository
import kotlinx.serialization.json.Json

class ParkingSignsRepositoryImpl(
    private val llmClient: LLMClient,
    private val ocrClient: OcrClient
): ParkingSignsRepository {

    override suspend fun analyzeParkingSigns(request: ParkingRequest): Result<ParkingResponse>  =
        try {
            val ocrResponse = ocrClient.analyzeParkingSigns(
                request.image,
                request.width,
                request.height,
                request.rotationDegrees,
                request.format
            )
            val llmResponse = llmClient.generateResponse(formatPrompt(ocrResponse))
            val parkingResponse = Json.decodeFromString<ParkingResponse>(llmResponse)
            Result.success(parkingResponse)
        } catch (e: Exception) {
            Result.failure(e)
        }

    override suspend fun analyzeParkingSigns(request: BitmapRequest): Result<ParkingResponse> =
        try {
            val ocrResponse = ocrClient.analyzeParkingSigns(request.encodedBitmap, request.rotationDegrees)
            val llmResponse = llmClient.generateResponse(formatPrompt(ocrResponse))
            val parkingResponse = Json.decodeFromString<ParkingResponse>(llmResponse)
            Result.success(parkingResponse)
        } catch (e: Exception) {
            Result.failure(e)
        }

    /**
     * This function takes  The text from an ocr analyzer and formats it into a prompt for the language model.
     * The prompt requests the language model to generate a response in the format of a ParkingResponse.
     * @param ocrResponse The text from the ocr analyzer.
     * @return A prompt for the language model.
     */
    private fun formatPrompt(ocrResponse: String): String {
        return """
            Can I park here? The text from the parking signs say: $ocrResponse. 
            If I can park, how long can I park for and how much does it cost?
            If I can't park, why not?
            Please respond in the following JSON format examples:
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