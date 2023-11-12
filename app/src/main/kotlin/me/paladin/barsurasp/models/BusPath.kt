package me.paladin.barsurasp.models

import kotlinx.serialization.Serializable

@Serializable
data class SavedPaths(
    var paths: List<BusPath> = listOf()
)

@Serializable
data class BusPath(
    val title: String,
    val buses: List<Triple<Int, String, Boolean>>
)