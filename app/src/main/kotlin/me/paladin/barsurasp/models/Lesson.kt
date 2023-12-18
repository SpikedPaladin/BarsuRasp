package me.paladin.barsurasp.models

import kotlinx.serialization.Serializable

sealed interface Lesson {
    val fullTime: String get() =
        when (this) {
            is Group -> time
            is Teacher -> time
        }

    @Serializable
    data class Group(
        val time: String,
        val sublessons: List<Sublesson>? = null,
        val replaced: Boolean = false
    ) : Lesson {
        val isEmpty: Boolean
            get() {
                return sublessons.isNullOrEmpty()
            }
    }

    @Serializable
    data class Teacher(
        val time: String,
        var name: String? = null,
        var type: String? = null,
        var groups: String? = null,
        var place: String? = null,
        var replaced: Boolean = false
    ) : Lesson {
        val isEmpty: Boolean
            get() {
                return name == null
            }
    }
}