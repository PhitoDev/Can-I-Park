package data

import domain.entities.Disclaimer
import domain.repositories.PreferencesDataSource
import domain.repositories.PreferencesRepository

class PreferencesRepositoryImpl(private val preferencesDataSource: PreferencesDataSource) : PreferencesRepository {
    override fun getDisclaimer(): Disclaimer = Disclaimer(
        """
            This app is for informational purposes only, and is highly experimental. It uses 
            generative AI to analyze parking signs and provide an estimate of whether you can park 
            in a given location. The developers make no guarantees as to the accuracy of the 
            information provided. The developers are not responsible for any fines or penalties 
            incurred by the user.
        """.trimIndent().replace("\n", " ")
    )

    override suspend fun markDisclaimerShown() {
        preferencesDataSource.setBooleanPreference(PreferencesDataSource.USER_HAS_SEEN_DISCLAIMER, true)
    }

    override suspend fun hasUserSeenDisclaimer(): Boolean {
        val result = preferencesDataSource.getBooleanPreference(PreferencesDataSource.USER_HAS_SEEN_DISCLAIMER)
        return if (result.isSuccess) {
            result.getOrThrow() as Boolean
        } else {
            false
        }
    }
}