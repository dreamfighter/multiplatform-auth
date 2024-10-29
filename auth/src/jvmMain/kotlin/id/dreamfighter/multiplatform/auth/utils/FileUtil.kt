package id.dreamfighter.multiplatform.auth.utils

import kotlinx.serialization.json.Json
import java.io.File
import java.text.MessageFormat


val JSON = Json { ignoreUnknownKeys = true }

object FileUtil {
    fun readFile(filePath: String): String? {
        return this::class.java.getResource(filePath)?.readText()
    }
    inline fun <reified T> String.toObject(): T = JSON.decodeFromString<T>(this)
}