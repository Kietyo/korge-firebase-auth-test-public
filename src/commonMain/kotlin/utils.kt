import korlibs.io.file.VfsFile
import kotlinx.serialization.json.Json

suspend inline fun <reified T> VfsFile.decodeJson(json: Json): T? {
    if (exists()) {
        return json.decodeFromString<T>(this.readString())
    }
    return null
}
