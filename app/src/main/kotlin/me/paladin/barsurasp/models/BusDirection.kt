package me.paladin.barsurasp.models

import kotlinx.serialization.Serializable

@Serializable
data class BusDirection(
    val busNumber: Int,
    val name: String,
    var backwardName: String? = null,
    val forward: Int,
    var backward: Int? = null
) {
    val hasBackward get() = backwardName != null

    @Serializable
    data class Wrapper(
        val lastFetch: String,
        val directions: List<BusDirection>
    )
}