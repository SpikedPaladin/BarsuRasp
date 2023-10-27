package me.paladin.barsurasp.models

import kotlinx.serialization.Serializable

@Serializable
data class Speciality(
    val name: String,
    val groups: List<String>
)