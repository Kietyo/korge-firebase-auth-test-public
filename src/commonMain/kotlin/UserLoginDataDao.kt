import korlibs.io.file.std.applicationVfs
import korlibs.io.file.std.resourcesVfs
import korlibs.logger.Logger
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

object UserLoginDataDao {
    private val mutex = Mutex()
    private val logger = Logger<UserLoginDataDao>()
    private val json = Json {
        ignoreUnknownKeys = true
        prettyPrint = true
    }

    val saveFileName = "user_login_data.json"

    suspend fun getUserLoginData(): UserLoginData {
        return mutex.withLock lock@{
            logger.info { "Getting user login data" }
            val file = applicationVfs[saveFileName]
            if (file.exists()) {
                return@lock file.decodeJson<UserLoginData.SignedIn>(json)!!
            }
            UserLoginData.None
        }
    }

    suspend fun saveUserLoginData(userLoginData: UserLoginData) {
        mutex.withLock {
            logger.info { "Saving user login data..." }
            val jsonData = json.encodeToString(userLoginData)
            applicationVfs[saveFileName].writeString(jsonData)
            logger.info { "Finished saving user login data." }
        }
    }
}
