package domain.repositories

import domain.entities.Disclaimer

interface DisclaimerRepository {
    fun getDisclaimer(): Disclaimer
    suspend fun markDisclaimerShown()
    suspend fun hasUserSeenDisclaimer(): Boolean
}