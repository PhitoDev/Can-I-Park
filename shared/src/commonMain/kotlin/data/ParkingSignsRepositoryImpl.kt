package data

import co.touchlab.kermit.Logger
import domain.entities.BitmapRequest
import domain.entities.ParkingRequest
import domain.entities.ParkingResponse
import domain.repositories.ParkingSignsRepository
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format
import kotlinx.datetime.format.DateTimeComponents
import kotlinx.datetime.format.DateTimeFormat
import kotlinx.datetime.format.DateTimeFormatBuilder
import kotlinx.datetime.format.FormatStringsInDatetimeFormats
import kotlinx.datetime.format.byUnicodePattern
import kotlinx.datetime.toLocalDateTime
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
            val parkingResponse = parkingResponseFromJson(llmResponse)
            Result.success(parkingResponse)
        } catch (e: Exception) {
            Logger.e { e.message.toString() }
            Result.failure(e)
        }

    override suspend fun analyzeParkingSigns(request: BitmapRequest): Result<ParkingResponse> =
        try {
            val ocrResponse = ocrClient.analyzeParkingSigns(request.encodedBitmap, request.rotationDegrees)
            val formattedPrompt = formatPrompt(ocrResponse)
            val llmResponse = llmClient.generateResponse(formattedPrompt)
            val parkingResponse = Json.decodeFromString<ParkingResponse>(llmResponse)
            Result.success(parkingResponse)
        } catch (e: Exception) {
            Logger.e { e.message.toString() }
            Result.failure(e)
        }

    private suspend fun parkingResponseFromJson(ocrResponse: String): ParkingResponse = try {
        val llmResponse = llmClient.generateResponse(formatPrompt(ocrResponse))
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
            Can I park here right now? It is ${getCurrentSystemTime()},
            and the text from the parking signs say: $ocrResponse. 
            If I can park, how long can I park? If there is a cost, how much does it cost?
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

    /**
     * This function returns the current system time in the format of "HH:mm a".
     * @return The current system time.
     */
    private fun getCurrentSystemTime(): String {
        val now: Instant = Clock.System.now()
        val localDateTime = now.toLocalDateTime(TimeZone.currentSystemDefault())
        return LocalTime.Format {
            amPmHour()
            chars(":")
            minute()
            chars(" ")
            amPmMarker("AM", "PM")
        }.format(localDateTime.time)
    }
}