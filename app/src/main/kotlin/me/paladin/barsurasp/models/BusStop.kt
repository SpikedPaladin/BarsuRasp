package me.paladin.barsurasp.models

import kotlinx.serialization.Serializable
import me.paladin.barsurasp.utils.getCurrentHour
import me.paladin.barsurasp.utils.getCurrentMinute
import me.paladin.barsurasp.utils.isWeekends
import org.jsoup.nodes.Element
import org.jsoup.select.Elements

@Serializable
data class BusStop(
    val name: String,
    var workdays: List<Time>? = null,
    var weekends: List<Time>? = null
) {
    val hasWorkdays get() = workdays != null
    val hasWeekends get() = weekends != null

    constructor(name: String, tables: Elements) : this(name) {
        workdays = parseTime(tables[0])
        weekends = parseTime(tables[1])
    }

    fun getNearestTime(limit: Int): List<Time>? {
        val hour = getCurrentHour()
        val minute = getCurrentMinute()

        if (isWeekends()) {
            if (weekends == null)
                return null

            return weekends!!.getNearestForTime(hour, minute, limit)
        } else {
            if (workdays == null)
                return null

            return workdays!!.getNearestForTime(hour, minute, limit)
        }
    }

    private fun List<Time>.getNearestForTime(hour: Int, minute: Int, limit: Int): List<Time> {
        val list = mutableListOf<Time>()

        for (time in this) {
            if (time.hour == hour && time.minute >= minute) {
                list += time
            } else if (time.hour > hour) {
                list += time
            }

            if (list.size == limit)
                break
        }

        return list
    }

    private fun parseTime(table: Element): List<Time>? {
        val tableElements = table.getElementsByTag("tr")

        if (tableElements.isNullOrEmpty())
            return null

        val schedule = mutableListOf<Time>()
        for (element in tableElements) {
            val minuteElement = element.getElementsByTag("td")[1]

            if (minuteElement.text().trim() == "")
                continue

            val hour = element.getElementsByTag("span")[0].text()

            for (minute in minuteElement.text().replace("[^0-9\\s]".toRegex(), "").trim().split(" ")) {
                schedule += Time(
                    hour =  hour.toInt(),
                    minute = minute.toInt()
                )
            }
        }
        return schedule
    }

    @Serializable
    data class Time(
        val hour: Int,
        val minute: Int
    ) {
        override fun toString(): String {
            return "${if (hour < 10) "0${hour}" else hour}:" +
                   "${if (minute < 10) "0${minute}" else minute}"
        }
    }

    companion object {

        fun List<Time>.hourSorted(): List<List<Time>> {
            var lastHour = -1
            val result = mutableListOf<List<Time>>()
            var list = mutableListOf<Time>()

            for (time in this) {
                if (time.hour > lastHour) {
                    lastHour = time.hour
                    list = mutableListOf()
                    result += list
                }

                list += time
            }

            return result
        }
    }
}