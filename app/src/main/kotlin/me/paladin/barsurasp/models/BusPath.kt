package me.paladin.barsurasp.models

import kotlinx.serialization.Serializable

@Serializable
data class BusPath(
    val title: String,
    val buses: List<Triple<Int, String, Boolean>>
) {

    @Serializable
    data class Wrapper(
        var paths: List<BusPath> = listOf()
    )
}