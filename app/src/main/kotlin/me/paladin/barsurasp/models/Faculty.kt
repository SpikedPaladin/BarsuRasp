package me.paladin.barsurasp.models

import kotlinx.serialization.Serializable

@Serializable
data class Faculty(
    val name: String,
    val specialities: List<Speciality>
) {

    @Serializable
    data class Wrapper(
        val lastFetch: String,
        val faculties: List<Faculty>
    )
}

fun List<Faculty>.forGroup(group: String): String {
    for (item in this) {
        item.specialities.forEach { speciality ->
            speciality.groups.forEach {
                if (it == group)
                    return item.name
            }
        }
    }

    return "Группа"
}