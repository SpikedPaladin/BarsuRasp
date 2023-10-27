package me.paladin.barsurasp.models

import kotlinx.serialization.Serializable

@Serializable
data class Sublesson(
    val type: String,
    val name: String,
    val place: String? = null,
    val teacher: String,
    val subgroup: String? = null
)