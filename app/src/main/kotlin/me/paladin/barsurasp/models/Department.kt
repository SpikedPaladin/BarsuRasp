package me.paladin.barsurasp.models

import kotlinx.serialization.Serializable

@Serializable
data class Department(
    val name: String,
    val teachers: List<String>
) {

    @Serializable
    data class Wrapper(
        val lastFetch: String,
        val departments: List<Department>
    )
}

fun List<Department>.forTeacher(teacher: String): String {
    for (item in this) {
        item.teachers.forEach {
            if (it == teacher)
                return item.name
        }
    }

    return "Преподаватель"
}