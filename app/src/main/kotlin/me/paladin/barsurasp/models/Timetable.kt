package me.paladin.barsurasp.models

import kotlinx.serialization.Serializable

@Serializable
data class Timetable(
    val days: List<DaySchedule>,
    val lastUpdate: String,
    val group: String,
    val date: String
) {
    fun getDayFromDate(apiDate: String): DaySchedule? {
        for (day in days) {
            if (day.date == apiDate)
                return day
        }

        return null
    }

    fun getDayNumberFromDate(apiDate: String): Int {
        for (i in days.indices) {
            if (days[i].date == apiDate)
                return i
        }

        return 0
    }
}