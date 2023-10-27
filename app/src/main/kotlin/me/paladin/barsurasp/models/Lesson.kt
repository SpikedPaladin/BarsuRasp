package me.paladin.barsurasp.models

import kotlinx.serialization.Serializable

@Serializable
data class Lesson(
    val time: String,
    val sublessons: List<Sublesson>? = null,
    val replaced: Boolean = false
) {
    val isEmpty: Boolean
        get() {
            return sublessons.isNullOrEmpty()
        }
}