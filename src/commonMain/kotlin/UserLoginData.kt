import korlibs.time.*
import kotlinx.serialization.Serializable

@Serializable
sealed class UserLoginData {

    @Serializable
    object None: UserLoginData()

    @Serializable
    data class SignedIn(
        val providerId: String,
        val emailVerified: Boolean,
        val displayName: String?,
        val idToken: String,
        // The number of seconds in which the ID token expires.
        val expiresInSeconds: Int,
        val refreshToken: String,
        val idTokenCreationTimeMillis: Long
    ): UserLoginData() {
        val expireTimeUnixMillis get() = idTokenCreationTimeMillis + expiresInSeconds * 1000
        fun isIdTokenExpired() = DateTime.nowUnixMillisLong() >= expireTimeUnixMillis
        fun updateWithNewIdToken(exchangeRefreshTokenForIdTokenResponse: ExchangeRefreshTokenForIdTokenResponse): SignedIn {
            return this.copy(
                idToken = exchangeRefreshTokenForIdTokenResponse.idToken,
                expiresInSeconds = exchangeRefreshTokenForIdTokenResponse.expiresInSeconds,
                refreshToken = exchangeRefreshTokenForIdTokenResponse.refreshToken,
                idTokenCreationTimeMillis = exchangeRefreshTokenForIdTokenResponse.idTokenCreationTimeMillis
            )
        }
    }
}

