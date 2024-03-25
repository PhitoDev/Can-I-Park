package di.modules

import CanIPark.shared.BuildConfig
import com.google.ai.client.generativeai.GenerativeModel
import data.AndroidLLMClient
import data.LLMClient
import org.koin.dsl.module

val androidModules = module {
    single { GenerativeModel(modelName = "gemini-pro-vision", apiKey = BuildConfig.API_KEY) }
    single<LLMClient> { AndroidLLMClient(get()) }
}