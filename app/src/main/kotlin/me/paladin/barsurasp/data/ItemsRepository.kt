package me.paladin.barsurasp.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import me.paladin.barsurasp.App
import me.paladin.barsurasp.data.loaders.GroupLoader
import me.paladin.barsurasp.data.loaders.TeacherLoader
import me.paladin.barsurasp.models.Department
import me.paladin.barsurasp.models.Faculty
import me.paladin.barsurasp.models.Item
import me.paladin.barsurasp.models.forGroup
import me.paladin.barsurasp.models.forTeacher
import me.paladin.barsurasp.utils.isGroup
import me.paladin.barsurasp.utils.loadFromFile
import me.paladin.barsurasp.utils.saveToFile
import java.io.File
import java.util.Calendar

object ItemsRepository {
    private const val FACULTIES_CACHE_FILE = "app/faculties.json"
    private const val DEPARTMENTS_CACHE_FILE = "app/departments.json"

    fun fetchFaculties(fromCache: Boolean = true): Flow<List<Faculty>> = flow {
        val faculties: List<Faculty>
        val file = getFacultiesFile()
        if (file.exists() && fromCache) {
            faculties = loadFromFile<Faculty.Wrapper>(file).faculties
        } else {
            faculties = GroupLoader.getFaculties()

            saveToFile(file, Faculty.Wrapper(Calendar.getInstance().time.toString(), faculties))
        }
        emit(faculties)
    }

    fun fetchDepartments(fromCache: Boolean = true): Flow<List<Department>> = flow {
        val departments: List<Department>
        val file = getDepartmentsFile()
        if (file.exists() && fromCache) {
            departments = loadFromFile<Department.Wrapper>(file).departments
        } else {
            departments = TeacherLoader.getDepartments()

            saveToFile(file, Department.Wrapper(Calendar.getInstance().time.toString(), departments))
        }
        emit(departments)
    }

    fun itemsFlow(): Flow<List<Item>> = flow {
        val faculties = fetchFaculties().first()
        val departments = fetchDepartments().first()

        val items = mutableListOf<Item>()

        for (faculty in faculties)
            for (speciality in faculty.specialities)
                for (group in speciality.groups)
                    items += Item(group, faculty.name)

        for (department in departments)
            for (teacher in department.teachers)
                items += Item(teacher, department.name)

        emit(items)
    }

    suspend fun getItemDescription(item: String): String {
        return try {
            if (isGroup(item)) {
                fetchFaculties().first().forGroup(item)
            } else {
                fetchDepartments().first().forTeacher(item)
            }
        } catch (_: Exception) {
            "Неизвестно"
        }
    }

    private fun getFacultiesFile() = File(App.getCacheDir(), FACULTIES_CACHE_FILE)
    private fun getDepartmentsFile() = File(App.getCacheDir(), DEPARTMENTS_CACHE_FILE)
}