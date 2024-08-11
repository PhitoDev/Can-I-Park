package domain.repositories

import domain.entities.Disclaimer

interface PreferencesRepository {
    fun getDisclaimer(): Disclaimer
    suspend fun markDisclaimerShown()
    suspend fun hasUserSeenDisclaimer(): Boolean
}