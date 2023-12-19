package me.paladin.barsurasp.models

import kotlinx.serialization.Serializable

sealed interface Timetable {
    val pageCount get() = when (this) {
        is Group -> days.size
        is Teacher -> days.size
    }

    val lastSiteUpdate get() = when (this) {
        is Group -> lastUpdate.emptyAsNull()
        is Teacher -> lastUpdate.emptyAsNull()
    }

    fun getDayNumberFromDate(apiDate: String): Int {
        when (this) {
            is Group -> {
                for (i in days.indices)
                    if (days[i].date == apiDate)
                        return i
            }
            is Teacher -> {
                for (i in days.indices)
                    if (days[i].date == apiDate)
                        return i
            }
        }

        return 0
    }

    private fun String.emptyAsNull() = if (this == "") null else this

    @Serializable
    data class Group(
        val days: List<DaySchedule.Group>,
        val lastUpdate: String,
        val group: String,
        val date: String
    ) : Timetable {
        fun getDayFromDate(apiDate: String): DaySchedule.Group? {
            for (day in days) {
                if (day.date == apiDate)
                    return day
            }

            return null
        }

        @Serializable
        data class Wrapper(
            var timetables: List<Group>
        )
    }

    @Serializable
    data class Teacher(
        val days: List<DaySchedule.Teacher>,
        val lastUpdate: String,
        val name: String,
        val date: String
    ) : Timetable {

        @Serializable
        data class Wrapper(
            var timetables: List<Teacher>
        )
    }
}