package data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import domain.entities.Disclaimer
import domain.repositories.PreferencesRepository
import kotlinx.coroutines.flow.first

class AndroidPreferencesRepository(
    private val dataStore: DataStore<Preferences>
) : PreferencesRepository {

    companion object {
        val USER_HAS_SEEN_DISCLAIMER = booleanPreferencesKey("USER_HAS_SEEN_DISCLAIMER")
    }

    override fun getDisclaimer(): Disclaimer = Disclaimer(
        """
            This app is for informational purposes only, and is highly experimental. It uses 
            generative AI to analyze parking signs and provide an estimate of whether you can park 
            in a given location. The developers make no guarantees as to the accuracy of the 
            information provided. The developers are not responsible for any fines or penalties 
            incurred by the user.
        """.trimIndent().replace("\n", " ")
    )

    override suspend fun markDisclaimerShown(): Result<Unit> =
        runCatching {
            dataStore.edit { preferences ->
                preferences[USER_HAS_SEEN_DISCLAIMER] = true
            }
        }

    override suspend fun hasUserSeenDisclaimer(): Result<Boolean> =
        runCatching {
            dataStore.data.first()[USER_HAS_SEEN_DISCLAIMER] ?: false
        }
}