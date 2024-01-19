import korlibs.io.net.*
import korlibs.io.net.http.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json


@Serializable
data class GoogleOauthTokenResponse(
    @SerialName("access_token")
    val accessToken: String,
    @SerialName("expires_in")
    val expiresInSecs: Int,
    @SerialName("refresh_token")
    val refreshToken: String,
    val scope: String,
    @SerialName("token_type")
    val tokenType: String,
    @SerialName("id_token")
    val idToken: String,
)

object GoogleOauthApi {
    //https://www.googleapis.com/auth/firebase

    // https://developers.google.com/identity/protocols/oauth2/web-server#exchange-authorization-code
    suspend fun requestAuth(
        clientId: String,
        clientSecret: String,
        // Note that this redirect URI doesn't seem to actually get visited
        // Example: "http://localhost:3000"
        redirectUri: String,
        code: String): GoogleOauthTokenResponse {
        val client = createHttpClient()

        //        val redirectUri = "https://random-td-5c564.firebaseapp.com/__/auth/handler"

        val codeEncoded = URL.encodeComponent(code)
        val redirectUriEncoded = URL.encodeComponent(redirectUri)

        val result =
            client.post(
                "https://oauth2.googleapis.com/token",
                HttpBodyContent.Companion.invoke(
                    "application/x-www-form-urlencoded",
                    "grant_type=authorization_code&code=$codeEncoded&client_id=$clientId&client_secret=$clientSecret&redirect_uri=$redirectUriEncoded"
                )
            )

        //        val codeEncoded = URL.encodeComponent(code)
        //        val redirectUriEncoded = URL.encodeComponent(redirectUri)
        //
        //        val result =
        //            client.post(
        //                "https://oauth2.googleapis.com/token",
        //                HttpBodyContent.Companion.invoke(
        //                    "application/x-www-form-urlencoded",
        //                    "grant_type=authorization_code&code=$codeEncoded&client_id=$clientId&client_secret=$clientSecret&redirect_uri=$redirectUriEncoded"
        //                )
        //            )
        // {"grant_type":"authorization_code","client_id":"$clientId","client_secret":"$clientSecret","redirect_uri":"$redirectUri","code":"$code"}

        //        val stringZ = result.content.readStringz()
        //        println("stringZ: $stringZ")
        //        val rawContentStringZ = result.rawContent.readStringz()
        //        println("rawContentStringZ: $rawContentStringZ")
        //        println(result.readAllString())

        val resultString = result.readAllString()

        val googleOauthTokenResponse = Json.decodeFromString<GoogleOauthTokenResponse>(resultString)
        println("googleOauthTokenResponse: $googleOauthTokenResponse")

        return googleOauthTokenResponse
    }

    //    suspend fun requestAuth() {
    //        val endpointString = "https://accounts.google.com/o/oauth2/v2"
    //        val client = createHttpClient()
    //        val redirectUri = "http://127.0.0.1:3000"
    //        val result =
    //            client.post(
    //                "https://accounts.google.com/o/oauth2/v2/auth",
    //                HttpBodyContent.Companion.invoke(
    //                    "application/json",
    //                    """
    //                {"client_id":"$clientId","redirect_uri":"$redirectUri","returnSecureToken":true}
    //            """
    //                        .trimIndent()
    //                )
    //            )
    //        println(result)
    //        println()
    //        println(result.content.readStringz())
    //        println()
    //        println(result.rawContent.readStringz())
    //        println()
    //        println(result.readAllString())
    //    }

}
