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