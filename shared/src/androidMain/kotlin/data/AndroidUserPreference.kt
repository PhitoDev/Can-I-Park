package data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.first

class AndroidUserPreference(private val dataStore: DataStore<Preferences>) : UserPreferences{
    override suspend fun hasUserSeenDisclaimer(): Boolean {
        return try {
            val preferences = dataStore.data.first()
            preferences[USER_HAS_SEEN_DISCLAIMER] ?: false
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun setUserHasSeenDisclaimer() {
        try {
            dataStore.edit { preferences ->
                preferences[USER_HAS_SEEN_DISCLAIMER] = true
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    companion object {
        val USER_HAS_SEEN_DISCLAIMER = booleanPreferencesKey(UserPreferences.USER_HAS_SEEN_DISCLAIMER)
    }
}