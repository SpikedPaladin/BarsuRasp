package me.paladin.barsurasp.data

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import me.paladin.barsurasp.App
import me.paladin.barsurasp.data.loaders.GroupLoader
import me.paladin.barsurasp.data.loaders.TeacherLoader
import me.paladin.barsurasp.models.BarsuFaculties
import me.paladin.barsurasp.models.Department
import me.paladin.barsurasp.models.Faculty
import java.io.File
import java.util.Calendar

object ItemsRepository {
    private const val FACULTIES_CACHE_FILE = "app/faculties.json"
    private const val DEPARTMENTS_CACHE_FILE = "app/departments.json"

    suspend fun getFaculties(useCache: Boolean = true): List<Faculty> {
        val faculties: List<Faculty>
        val file = getFacultiesFile()
        if (file.exists() && useCache) {
            faculties = Json.decodeFromString<BarsuFaculties>(file.readText()).faculties
        } else {
            faculties = GroupLoader.getFaculties()

            file.parentFile?.mkdirs()
            file.writeText(Json.encodeToString(BarsuFaculties(Calendar.getInstance().time.toString(), faculties)))
        }
        return faculties
    }

    suspend fun getDepartments(useCache: Boolean = true): List<Department> {
        val departments: List<Department>
        val file = getDepartmentsFile()
        if (file.exists() && useCache) {
            departments = Json.decodeFromString<Department.Wrapper>(file.readText()).departments
        } else {
            departments = TeacherLoader.getDepartments()

            file.parentFile?.mkdirs()
            file.writeText(Json.encodeToString(Department.Wrapper(Calendar.getInstance().time.toString(), departments)))
        }

        return departments
    }

    private fun getFacultiesFile() = File(App.getCacheDir(), FACULTIES_CACHE_FILE)
    private fun getDepartmentsFile() = File(App.getCacheDir(), DEPARTMENTS_CACHE_FILE)
}