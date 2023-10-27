package me.paladin.barsurasp.models

import kotlinx.serialization.Serializable

@Serializable
data class DaySchedule(
    val date: String,
    val dayOfWeek: String,
    val week: String? = null,
    var lessons: List<Lesson>? = null
) {
    val actualLessons: List<Lesson>?
        get() {
            if (lessons.isNullOrEmpty())
                return null

            val actualList = mutableListOf<Lesson>()

            for (lesson in lessons!!) if (!lesson.isEmpty)
                actualList += lesson

            return actualList
        }
    val lastIndex: Int
        get() {
            lessons?.let {
                it.reversed().forEachIndexed { index, item ->
                    if (!item.isEmpty)
                        return index
                }
            }

            return 0
        }
}