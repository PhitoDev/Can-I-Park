package di.modules

import CanIPark.shared.BuildConfig
import com.google.ai.client.generativeai.GenerativeModel
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import data.AndroidLLMClient
import data.AndroidOcrClient
import data.LLMClient
import data.OcrClient
import org.koin.dsl.module

val androidModules = module {
    single { TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS) }
    single { GenerativeModel(modelName = "gemini-pro-vision", apiKey = BuildConfig.API_KEY) }
    single<OcrClient> { AndroidOcrClient(get()) }
    single<LLMClient> { AndroidLLMClient(get()) }
}