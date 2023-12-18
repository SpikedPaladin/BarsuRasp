package me.paladin.barsurasp.models

import kotlinx.serialization.Serializable

sealed interface DaySchedule {
    val day: String get() =
        when (this) {
            is Group -> dayOfWeek
            is Teacher -> dayOfWeek
        }

    val fullDate: String get() =
        when (this) {
            is Group -> date
            is Teacher -> date
        }

    val actualLessons: List<Lesson>?
        get() {
            when (this) {
                is Group -> {
                    if (lessons.isNullOrEmpty())
                        return null

                    val actualList = mutableListOf<Lesson.Group>()

                    for (lesson in lessons!!) if (!lesson.isEmpty)
                        actualList += lesson

                    return actualList
                }

                is Teacher -> {
                    if (lessons.isNullOrEmpty())
                        return null

                    val actualList = mutableListOf<Lesson.Teacher>()

                    for (lesson in lessons!!) if (!lesson.isEmpty)
                        actualList += lesson

                    return actualList
                }
            }
        }

    @Serializable
    data class Group(
        val date: String,
        val dayOfWeek: String,
        val week: String? = null,
        var lessons: List<Lesson.Group>? = null
    ) : DaySchedule

    @Serializable
    data class Teacher(
        val date: String,
        val dayOfWeek: String,
        var lessons: List<Lesson.Teacher>? = null
    ) : DaySchedule
}