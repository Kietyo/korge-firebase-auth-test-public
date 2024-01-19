import korlibs.io.async.*
import korlibs.io.net.http.*
import kotlinx.coroutines.*

data class GoogleOauthFlowResult(
    val code: String,
    val scope: List<String>,
    val state: String?
)

object GoogleOauthFlow {

    suspend fun oauthFlow(oauthRequest: GoogleOauthRequest, handleSignInUrl: suspend (signInUrl: String) -> Unit = {}): GoogleOauthFlowResult {
        val signInUrl = oauthRequest.createUrl()
        println("""
            Please visit this URL to sign into Google!
            ${signInUrl}
        """.trimIndent())

        handleSignInUrl(oauthRequest.createUrl())

//        views.gameWindow.browse(URL.invoke(oauthRequest.createUrl()))

        val oauthResultDeferred = async(Dispatchers.CIO) {
            var oauthResultInternal: GoogleOauthFlowResult? = null
            val server = createHttpServer {
                this.listen(port = 3000) {
                    println("Received request for server.")
                    println(it.uri)
                    println(it.absoluteURI)
                    println(it.getParams)
                    val code = it.getParams["code"]!!.run {
                        require(size == 1)
                        get(0)
                    }
                    val scope = it.getParams["scope"]!!
                    val state = it.getParams["state"]?.run {
                        require(size == 1)
                        get(0)
                    }
                    oauthResultInternal = GoogleOauthFlowResult(
                        code,
                        scope,
                        state
                    )
                    it.write("""
                    <!DOCTYPE html>
                    <html>
                        <body>
                            OAUTH token received! You may close this tab now. Thanks!
                        </body>
                    </html>
                """.trimIndent())
                    it.end()
                }
            }

            while (oauthResultInternal == null) {
                delay(100)
            }

            server.close()

            println("got oauthResultInternal: $oauthResultInternal")

            oauthResultInternal!!
        }

        val oauthResult = oauthResultDeferred.await()

        println("""
            Got oauth result! $oauthResult
        """.trimIndent())

        return oauthResult
    }
}
