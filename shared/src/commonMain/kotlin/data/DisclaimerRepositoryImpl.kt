package data

import domain.entities.Disclaimer
import domain.repositories.DisclaimerRepository

class DisclaimerRepositoryImpl(private val userPreferences: UserPreferences) : DisclaimerRepository {
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
        userPreferences.setUserHasSeenDisclaimer()
    }

    override suspend fun hasUserSeenDisclaimer(): Boolean = userPreferences.hasUserSeenDisclaimer()
}