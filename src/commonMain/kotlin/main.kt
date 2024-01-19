import korlibs.korge.*
import korlibs.korge.scene.*
import korlibs.korge.view.*
import korlibs.image.color.*
import korlibs.image.text.RichTextData
import korlibs.io.net.*
import korlibs.korge.input.*
import korlibs.korge.ui.*
import korlibs.korge.view.align.*
import korlibs.math.geom.*
import korlibs.render.*
import korlibs.time.DateTime
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

suspend fun main() = Korge(windowSize = Size(512, 512), backgroundColor = Colors["#2b2b2b"]) {
	val sceneContainer = sceneContainer()

	sceneContainer.changeTo({ MyScene() })
}

class MyScene : Scene() {
    private val json = Json { prettyPrint = true }
    val firebaseRestApi = FirebaseRestApi(Constants.firebaseApiKey)

	override suspend fun SContainer.sceneMain() {

        var currentUserData = UserLoginDataDao.getUserLoginData()

        when (currentUserData) {
            UserLoginData.None -> {
                showSignInButton()
            }
            is UserLoginData.SignedIn -> {
                if (currentUserData.isIdTokenExpired()) {
                    currentUserData = refreshIdToken(currentUserData)
                    UserLoginDataDao.saveUserLoginData(currentUserData)
                }
                showLoginData(currentUserData)
            }
        }
//		while (true) {
//			image.tween(image::rotation[minDegrees], time = 1.seconds, easing = Easing.EASE_IN_OUT)
//			image.tween(image::rotation[maxDegrees], time = 1.seconds, easing = Easing.EASE_IN_OUT)
//		}
	}

    private suspend fun refreshIdToken(userLoginData: UserLoginData.SignedIn): UserLoginData.SignedIn {
        val refreshResult = firebaseRestApi.exchangeRefreshTokenForIdToken(userLoginData.refreshToken)
        return userLoginData.updateWithNewIdToken(refreshResult)
    }

    private fun SContainer.showSignInButton() {
        val oauthRequest = GoogleOauthRequest(
            Constants.oauthClientId,
            "http://localhost:3000",
            ResponseType.CODE,
            "state1234"
        )

        val button = uiButton("Sign in using Google", size = Size(140, 50)) {
            centerOnStage()
            onClick {
                val oauthFlowResponse = GoogleOauthFlow.oauthFlow(oauthRequest) {
                    views.gameWindow.browse(URL.invoke(it))
                }

                val oauthResponse = GoogleOauthApi.requestAuth(
                    Constants.oauthClientId,
                    Constants.oauthClientSecret,
                    "http://localhost:3000",
                    oauthFlowResponse.code
                )
                println("oauthResponse: $oauthResponse")

                val signInWithIdpResponse = firebaseRestApi.signInWithIdp(oauthResponse.idToken)
                println("signInWithIdpResponse: $signInWithIdpResponse")

                val userData = signInWithIdpResponse.toUserLoginData()

                UserLoginDataDao.saveUserLoginData(userData)

                this@uiButton.removeFromParent()

                this@showSignInButton.showLoginData(userData)
            }
        }

        val button2 = uiButton("Sign in anonymously", size = Size(140, 50)) {
            centerOnStage()
            alignTopToBottomOf(button)

            onClick {
                val signInAnonymouslyResponse = firebaseRestApi.signInAnonymously()
                println("signInAnonymouslyResponse: $signInAnonymouslyResponse")

                firebaseRestApi.getUserData(signInAnonymouslyResponse.idToken)
            }
        }
    }

    private fun SContainer.showLoginData(currentUserData: UserLoginData.SignedIn) {
        textBlock() {
            val pretty = json.encodeToString(currentUserData)
            text = RichTextData(
                """
                    You are logged in as: ${currentUserData.displayName}
                    More info: ${pretty}
                    DateTime logged in: ${DateTime.fromUnixMillis(currentUserData.idTokenCreationTimeMillis).toStringDefault()}
                """.trimIndent()
            )
            autoSize = true
            scale = Scale(0.5)
        }
    }
}

private fun SignInWithIdpResponse.toUserLoginData() =
    UserLoginData.SignedIn(
        providerId,
        emailVerified,
        displayName,
        idToken,
        expiresInSeconds,
        refreshToken,
        loggedInTimeUnixMillis
    )
