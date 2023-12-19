package me.paladin.barsurasp.models

import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

sealed interface Timetable {
    val pageCount get() = when (this) {
        is Group -> days.size
        is Teacher -> days.size
    }

    val lastSiteUpdate get() = when (this) {
        is Group -> lastUpdate.emptyAsNull()
        is Teacher -> lastUpdate.emptyAsNull()
    }

    fun getDayFromDate(apiDate: String): DaySchedule? {
        when (this) {
            is Group -> for (day in days) {
                if (day.date == apiDate)
                    return day
            }
            is Teacher -> for (day in days) {
                if (day.date == apiDate)
                    return day
            }
        }
        return null
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

    fun encode(json: Json = Json): String = when (this) {
        is Group -> json.encodeToString(this)
        is Teacher -> json.encodeToString(this)
    }

    private fun String.emptyAsNull() = if (this == "") null else this

    @Serializable
    data class Group(
        val days: List<DaySchedule.Group>,
        val lastUpdate: String,
        val group: String,
        val date: String
    ) : Timetable {

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

    companion object {

        fun decode(input: String, isGroup: Boolean, json: Json = Json): Timetable = when (isGroup) {
            true -> json.decodeFromString<Group>(input)
            false -> json.decodeFromString<Teacher>(input)
        }
    }
}