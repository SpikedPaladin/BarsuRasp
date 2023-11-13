package me.paladin.barsurasp.data

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import me.paladin.barsurasp.App
import me.paladin.barsurasp.data.loaders.FacultyLoader
import me.paladin.barsurasp.models.BarsuFaculties
import me.paladin.barsurasp.models.Faculty
import java.io.File
import java.util.Calendar

object FacultyRepository {
    private const val CACHE_FILE = "app/faculties.json"

    suspend fun getFaculties(useCache: Boolean = true): List<Faculty> {
        val faculties: List<Faculty>
        val file = getFacultiesFile()
        if (file.exists() && useCache) {
            faculties = Json.decodeFromString<BarsuFaculties>(file.readText()).faculties
        } else {
            faculties = FacultyLoader.getFaculties()

            file.parentFile?.mkdirs()
            file.writeText(Json.encodeToString(BarsuFaculties(Calendar.getInstance().time.toString(), faculties)))
        }
        return faculties
    }

    private fun getFacultiesFile() = File(App.getCacheDir(), CACHE_FILE)
}