package data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import domain.repositories.PreferencesDataSource
import kotlinx.coroutines.flow.first

class AndroidPreferenceDataSource(private val dataStore: DataStore<Preferences>) : PreferencesDataSource {
    override suspend fun getBooleanPreference(key: String): Result<Any> =
        runCatching { dataStore.data.first()[booleanPreferencesKey(key)] ?: false }

    override suspend fun setBooleanPreference(key: String, value: Boolean): Result<Unit> =
        runCatching {
            dataStore.edit { preferences ->
                preferences[booleanPreferencesKey(key)] = value
            }
        }

    companion object {
        val USER_HAS_SEEN_DISCLAIMER = booleanPreferencesKey(PreferencesDataSource.USER_HAS_SEEN_DISCLAIMER)
    }
}