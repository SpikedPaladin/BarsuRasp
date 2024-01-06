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

        val splitGroups: String?
            get() {
                if (groups == null)
                    return null

                var result = groups!!
                val pattern = "[а-я] [А-Я]".toRegex()
                while (true) {
                    val found = pattern.find(result)
                    if (found != null) {
                        val spaceIndex = found.range.first + 1

                        result = listOf(result.substring(0, spaceIndex), result.substring(spaceIndex + 1)).joinToString(separator = "\n")
                    } else break
                }
                return result
            }
    }
}