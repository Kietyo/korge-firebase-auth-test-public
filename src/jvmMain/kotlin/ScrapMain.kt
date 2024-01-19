import com.google.auth.oauth2.*
import com.google.firebase.*
import com.google.firebase.auth.*
import korlibs.image.color.*
import korlibs.korge.*
import kotlinx.coroutines.*

object ScrapMain {
  @JvmStatic
  fun main(args: Array<String>) = runBlocking {
    Korge(
        virtualWidth = 512, virtualHeight = 512, bgcolor = Colors["#2b2b2b"]) {
//        val firebaseOptions = FirebaseOptions.builder()
//            .setProjectId("projectid")
//            .setCredentials(GoogleCredentials.getApplicationDefault())
//            .build()
//        FirebaseApp.initializeApp(firebaseOptions)
//        val firebaseAuth = FirebaseAuth.getInstance()
//
//        val user = firebaseAuth.createUser(
//            UserRecord.CreateRequest()
//            .setEmail("asdf@gmail.com")
//            .setPassword("passwordtest123"))
//        println(user)
//
//
//        val server = createHttpServer {
//            this.listen(port = 3000) {
//                println("Received request for server.")
//
//                println(it)
//
//                println(it.method)
//
//                println(it.requestConfig)
//
//                println(it.absoluteURI)
//
//                println(it.readRawBody().decodeToString())
//
//                println(it.getParams)
//
//                it.write("""
//                    <!DOCTYPE html>
//                    <html>
//                        <body>
//                            OAUTH token received! You may close this tab now. Thanks!
//                        </body>
//                    </html>
//                """.trimIndent())
//
//                it.end()
//            }
//        }
//        println("Started server!")
//        FirebaseRestApi.createAuthUri(apiKey, "afadfdsfsf@gmail.com", "https://google.com")
//
//        val oauthRequest = GoogleOauthRequest(
//            Constants.oauthClientId,
//            "http://localhost:3000",
//            ResponseType.CODE,
//            "state1234"
//        )
//        val oauthFlowResponse = GoogleOauthFlow.oauthFlow(oauthRequest)
//        val oauthResponse = GoogleOauthApi.requestAuth(
//            Constants.oauthClientId,
//            Constants.oauthClientSecret,
//            "http://localhost:3000",
//            oauthFlowResponse.code
//        )
//
//        val firebaseRestApi = FirebaseRestApi(apiKey)
//        firebaseRestApi.signInWithIdp(oauthResponse.idToken)

    }
  }
}
