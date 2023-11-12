package me.paladin.barsurasp.models

import kotlinx.serialization.Serializable

@Serializable
data class SavedDirections(
    val lastFetch: String,
    val directions: List<BusDirection>
)

@Serializable
data class BusDirection(
    val busNumber: Int,
    val name: String,
    var backwardName: String? = null,
    val forward: Int,
    var backward: Int? = null
)