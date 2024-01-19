import korlibs.io.net.*

enum class ResponseType(val str: String) {
    CODE("code")
}

data class GoogleOauthRequest(
    val clientId: String,
    val redirectUri: String,
    val responseType: ResponseType,
    val state: String? = null
) {
    companion object {
        val OAUTH_URL = "https://accounts.google.com/o/oauth2/v2/auth"
    }

    fun createUrl(): String {
        val paramMap = buildMap {
            this["access_type"] = "offline"
            this["include_granted_scopes"] = "true"
            this["client_id"] = clientId
            this["response_type"] = responseType.str
            this["redirect_uri"] = URL.encodeComponent(redirectUri)
            this["scope"] = "profile"
            state?.let {
                this["state"] = it
            }
        }

        return buildString {
            append(OAUTH_URL)
            append("?")
            append(paramMap.map { it.key + "=" + it.value }.joinToString("&") { it })
        }
    }
}
