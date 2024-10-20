package id.dreamfighter.multiplatform.auth.utils

import id.dreamfighter.multiplatform.auth.applicationContext
import kotlinx.serialization.json.Json


object FileUtil {
    fun readFile(filePath: String):String = applicationContext.assets.open(filePath).bufferedReader().use { it.readText() }
    inline fun <reified T> String.toObject(): T = Json.decodeFromString<T>(this)
}