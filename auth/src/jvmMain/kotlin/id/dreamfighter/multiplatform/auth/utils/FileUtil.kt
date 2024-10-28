package id.dreamfighter.multiplatform.auth.utils

import kotlinx.serialization.json.Json
import java.nio.file.FileSystem

val JSON = Json { ignoreUnknownKeys = true }

object FileUtil {
    fun readFile(filePath: String): String? {
        println("path = ${this::class.java.getResource(filePath)?.path}")
        return this::class.java.getResource(filePath)?.readText()
    }
    inline fun <reified T> String.toObject(): T = JSON.decodeFromString<T>(this)
}