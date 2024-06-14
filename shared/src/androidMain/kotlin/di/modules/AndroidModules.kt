package di.modules

import CanIPark.shared.BuildConfig
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.google.ai.client.generativeai.GenerativeModel
import data.AndroidLLMClient
import data.AndroidUserPreference
import data.LLMClient
import data.UserPreferences
import org.koin.dsl.module

val androidModules = module {
    single { GenerativeModel(modelName = "gemini-1.5-flash", apiKey = BuildConfig.API_KEY) }
    single<UserPreferences> { AndroidUserPreference(get()) }
    single<LLMClient> { AndroidLLMClient(get()) }
}