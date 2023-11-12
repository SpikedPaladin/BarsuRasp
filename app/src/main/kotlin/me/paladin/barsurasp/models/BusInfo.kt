package me.paladin.barsurasp.models

import kotlinx.serialization.Serializable

@Serializable
data class BusInfo(
    val lastFetch: String,
    val number: Int,
    val name: String,
    val backwardName: String? = null,
    val stops: List<BusStop>,
    val backwardStops: List<BusStop>? = null
) {
    val hasBackward = backwardName != null

    fun getStop(name: String, backward: Boolean): BusStop {
        return if (backward) {
            backwardStops!!.find { it.name == name }!!
        } else {
            stops.find { it.name == name }!!
        }
    }
}
