package domain.repositories

import domain.entities.Disclaimer

interface PreferencesRepository {
    fun getDisclaimer(): Disclaimer
    suspend fun markDisclaimerShown(): Result<Unit>
    suspend fun hasUserSeenDisclaimer(): Result<Boolean>
}