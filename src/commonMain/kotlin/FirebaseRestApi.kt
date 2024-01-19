import korlibs.io.net.*
import korlibs.io.net.http.*
import korlibs.time.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.*

@Serializable
data class SignInWithIdpResponse(
    val federatedId: String,
    val providerId: String,
    val localId: String,
    val emailVerified: Boolean,
    val email: String? = null,
    val oauthIdToken: String?,
    val oauthAccessToken: String? = null,
    val oauthTokenSecret: String? = null,
    val rawUserInfo: String,
    val firstName: String?,
    val lastName: String? = null,
    val fullName: String?,
    val displayName: String?,
    val photoUrl: String?,
    val idToken: String,
    val refreshToken: String,
    @SerialName("expiresIn")
    val expiresInSeconds: Int,
    val needConfirmation: Boolean? = null,
    val kind: String,
    val loggedInTimeUnixMillis: Long = DateTime.nowUnixMillisLong()
)

@Serializable
data class SignInAnonymouslyResponse(
    val idToken: String,
    val email: String? = null,
    val refreshToken: String,
    @SerialName("expiresIn")
    val expiresInSeconds: Int,
    val localId: String,
    val loggedInTimeUnixMillis: Long = DateTime.nowUnixMillisLong()
)

@Serializable
data class ExchangeRefreshTokenForIdTokenResponse(
    @SerialName("access_token")
    val accessToken: String,
    @SerialName("expires_in")
    val expiresInSeconds: Int,
    @SerialName("token_type")
    val tokenType: String,
    @SerialName("refresh_token")
    val refreshToken: String,
    @SerialName("id_token")
    val idToken: String,
    @SerialName("user_id")
    val userId: String,
    @SerialName("project_id")
    val projectId: String,
    val idTokenCreationTimeMillis: Long = DateTime.nowUnixMillisLong()
)

@Serializable
data class ProviderUserInfo(
    val providerId: String,
    val federatedId: String
)

@Serializable
data class UserObject(
    // The uid of the current user.
    val localId: String,
    // The email of the account.
    val email: String? = null,
    // Whether or not the account's email has been verified.
    val emailVerified: Boolean? = null,
    // The display name for the account.
    val displayName: String? = null,
    // List of JSON objects	List of all linked provider objects which contain "providerId" and "federatedId".
    val providerUserInfo: List<ProviderUserInfo>? = null,
    // The photo Url for the account.
    val photoUrl: String? = null,
    // Hash version of password.
    val passwordHash: String? = null,
    // The timestamp, in milliseconds, that the account password was last changed.
    val passwordUpdatedAt: Double? = null,
    // The timestamp, in seconds, which marks a boundary, before which Firebase ID token are considered revoked.
    val validSince: String? = null,
    // Whether the account is disabled or not.
    val disabled: Boolean? = null,
    // The timestamp, in milliseconds, that the account last logged in at.
    val lastLoginAt: String,
    // The timestamp, in milliseconds, that the account was created at.
    val createdAt: String,
    // Whether the account is authenticated by the developer.
    val customAuth: Boolean? = null,
)

@Serializable
data class GetUserDataResponse(
    val users: List<UserObject>,
)

/**
 * https://firebase.google.com/docs/reference/rest/auth
 */
class FirebaseRestApi(private val apiKey: String) {
    val httpClient = createHttpClient()
    private val endpoint = createHttpClientEndpoint(endpointString)
    private val restClient = HttpRestClient(endpoint)

    suspend fun signInWithCustomToken(token: String) {
        val path = ":signInWithCustomToken?key=$apiKey"
        val result = restClient.post(
            path,
            """
                {"token":"$token","returnSecureToken":true}
            """.trimIndent()
        )
        println(result)
    }

    suspend fun getUserData(idToken: String) {
        val path = ":lookup?key=$apiKey"
        val result = restClient.post(
            path,
            """
                {"idToken":"$idToken"}
            """.trimIndent()
        )
        println(result)

        val result2= httpClient.post("$endpointString$path",
            HttpBodyContent(
                "application/json",
                """
                {"idToken":"$idToken"}
            """.trimIndent()
            )
            )
        println(result2)

        val res = jsonParser.decodeFromString<GetUserDataResponse>(result2.readAllString())

        println(res)
    }

    suspend fun exchangeRefreshTokenForIdToken(refreshToken: String): ExchangeRefreshTokenForIdTokenResponse {
        val path = "key=$apiKey"
        val result = httpClient.post(
            "$secureTokenUrl?$path",
            HttpBodyContentFormUrlEncoded(
                "grant_type" to "refresh_token",
                "refresh_token" to refreshToken
            )
        )
        println(result)

        val resultString = result.readAllString()
        println(resultString)

        val res = jsonParser.decodeFromString<ExchangeRefreshTokenForIdTokenResponse>(resultString)
        println(res)
        return res
    }

    suspend fun signInWithPassword(username: String, password: String) {
        val path = ":signInWithPassword?key=$apiKey"
        val result = restClient.post(
            path,
            """
                {"email":"$username","password":"$password","returnSecureToken":true}
            """.trimIndent()
        )
        println(result)

        val resultString = (result as HttpClient.Response).readAllString()
        println(resultString)
    }

    private val jsonParser = Json { ignoreUnknownKeys = true }

    suspend fun signInWithIdp(googleIdToken: String): SignInWithIdpResponse {
        println("signInWithIdp")
        val path = ":signInWithIdp?key=$apiKey"
        val googleIdTokenEncoded = URL.encodeComponent(googleIdToken)

        val result = httpClient.post(
            "$endpointString$path",
            HttpBodyContent(
                "application/json",
                """
                {"postBody":"id_token=$googleIdTokenEncoded&providerId=google.com","requestUri":"https://random-td-5c564.firebaseapp.com","returnIdpCredential":true,"returnSecureToken":true}
            """.trimIndent()
            )

        )
        val resultString = result.readAllString()
        return jsonParser.decodeFromString<SignInWithIdpResponse>(resultString)
    }

    suspend fun signInAnonymously(): SignInAnonymouslyResponse {
        println("signUp")
        val path = ":signUp?key=$apiKey"

        val result = httpClient.post(
            "$endpointString$path",
            HttpBodyContent(
                "application/json",
                """
                {"returnSecureToken":true}
            """.trimIndent()
            )

        )
        val resultString = result.readAllString()
        return jsonParser.decodeFromString<SignInAnonymouslyResponse>(resultString)
    }

    suspend fun createAuthUri(email: String, continueUrl: String) {
        val path = ":createAuthUri?key=$apiKey"
        val result = restClient.post(
            path,
            """
                {"identifier":"$email", "continueUri": "$continueUrl"}
            """.trimIndent()
        )
        println(result)
    }

    suspend fun signup(email: String, password: String) {
        val path = ":signUp?key=$apiKey"
        val result = restClient.post(
            path,
            """
                {"email":"$email","password":"$password","returnSecureToken":true}
            """.trimIndent()
        )
        println(result)
    }

    companion object {
        const val endpointString = "https://identitytoolkit.googleapis.com/v1/accounts/"
        const val secureTokenUrl = "https://securetoken.googleapis.com/v1/token"
    }
}
