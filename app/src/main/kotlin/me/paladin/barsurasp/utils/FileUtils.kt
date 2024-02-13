package me.paladin.barsurasp.utils

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File

val json = Json { ignoreUnknownKeys = true }

suspend inline fun <reified T> loadFromFile(file: File): T =
    withContext(Dispatchers.IO) {
        json.decodeFromString<T>(file.readText())
    }

suspend inline fun <reified T> saveToFile(file: File, content: T) = withContext(Dispatchers.IO) {
    file.parentFile?.mkdirs()
    file.writeText(json.encodeToString(content))
}