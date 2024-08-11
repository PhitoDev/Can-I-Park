package di.modules

import CanIPark.shared.BuildConfig
import com.google.ai.client.generativeai.GenerativeModel
import data.AndroidLLMDataSource
import data.AndroidPreferenceDataSource
import domain.repositories.LLMDataSource
import domain.repositories.PreferencesDataSource
import org.koin.dsl.module

val androidModules = module {
    single { GenerativeModel(modelName = "gemini-1.5-flash", apiKey = BuildConfig.API_KEY) }
    single<PreferencesDataSource> { AndroidPreferenceDataSource(get()) }
    single<LLMDataSource> { AndroidLLMDataSource(get()) }
}