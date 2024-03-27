package data

interface UserPreferences {
    suspend fun hasUserSeenDisclaimer(): Boolean

    suspend fun setUserHasSeenDisclaimer()

    companion object {
        const val USER_HAS_SEEN_DISCLAIMER = "USER_HAS_SEEN_DISCLAIMER"
    }
}