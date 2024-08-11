package di.modules

import CanIPark.shared.BuildConfig
import com.google.ai.client.generativeai.GenerativeModel
import data.AndroidCameraRepository
import data.AndroidLLMRepository
import data.AndroidPreferencesRepository
import domain.repositories.CameraRepository
import domain.repositories.LLMRepository
import domain.repositories.PreferencesRepository
import org.koin.dsl.module

val androidModules = module {
    single { GenerativeModel(modelName = "gemini-1.5-pro", apiKey = BuildConfig.API_KEY) }
    single<PreferencesRepository> { AndroidPreferencesRepository(get()) }
    single<CameraRepository> { AndroidCameraRepository(get()) }
    single<LLMRepository> { AndroidLLMRepository(get()) }
}