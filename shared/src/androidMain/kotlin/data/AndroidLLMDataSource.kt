package data

import android.graphics.BitmapFactory
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.Content
import domain.entities.ImageDetails
import domain.repositories.LLMDataSource
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

class AndroidLLMDataSource(private val generativeModel: GenerativeModel) : LLMDataSource {

    @OptIn(ExperimentalEncodingApi::class)
    override suspend fun generateResponse(prompt: String, imageDetails: ImageDetails): String {
        val builder = Content.Builder()
        val  byteArray = Base64.decode(imageDetails.encodedBitmap)
        val bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
        builder.image(bitmap)
        builder.text(prompt)
        val response = generativeModel.generateContent(builder.build())
        return response.text ?: throw IllegalStateException("Response is null")
    }
}