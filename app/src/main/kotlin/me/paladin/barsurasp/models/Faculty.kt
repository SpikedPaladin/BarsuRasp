package me.paladin.barsurasp.models

import kotlinx.serialization.Serializable

@Serializable
data class Faculty(
    val name: String,
    val specialities: List<Speciality>
)