package domain.repositories

interface PreferencesDataSource {
    suspend fun getBooleanPreference(key: String): Result<Any>

    suspend fun setBooleanPreference(key: String, value: Boolean): Result<Unit>

    companion object {
        const val USER_HAS_SEEN_DISCLAIMER = "USER_HAS_SEEN_DISCLAIMER"
    }
}