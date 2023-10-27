package me.paladin.barsurasp.models

import kotlinx.serialization.Serializable

@Serializable
data class BarsuFaculties(
    val lastFetch: String,
    val faculties: List<Faculty>
)