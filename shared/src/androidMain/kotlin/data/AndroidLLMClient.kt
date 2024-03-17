package data

import android.graphics.BitmapFactory
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.Content
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

class AndroidLLMClient(private val generativeModel: GenerativeModel) : LLMClient {

    @OptIn(ExperimentalEncodingApi::class)
    override suspend fun generateResponse(
        prompt: String,
        encodedBitmap: String?,
        rotationDegrees: Int
    ): String {
        val builder = Content.Builder()
        if (encodedBitmap != null) {
            val  byteArray = Base64.decode(encodedBitmap)
            val bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
            builder.image(bitmap)
        } else {
            builder.text(prompt)
        }
        val response = generativeModel.generateContent(builder.build())
        return response.text ?: throw IllegalStateException("Response is null")
    }
}