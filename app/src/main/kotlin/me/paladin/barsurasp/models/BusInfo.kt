package me.paladin.barsurasp.models

import kotlinx.serialization.Serializable
import me.paladin.barsurasp.models.BusStop.Companion.getNearestStop

@Serializable
data class BusInfo(
    val lastFetch: String,
    val number: Int,
    val name: String,
    val backwardName: String? = null,
    val stops: List<BusStop>,
    val backwardStops: List<BusStop>? = null
) {
    fun getStop(name: String, backward: Boolean): BusStop {
        return if (backward) {
            backwardStops!!.find { it.name == name }!!
        } else {
            stops.find { it.name == name }!!
        }
    }

    fun getTimeline(
        time: BusStop.Time,
        name: String,
        backward: Boolean,
        workdays: Boolean
    ): List<TimelineElement> {
        val list = if (backward) backwardStops!! else stops
        val stopIndex = list.indexOf(list.find { it.name == name }!!)
        val timeline = mutableListOf<TimelineElement>()

        // Split into two parts that not include selected stop
        val passedList = list.slice(0..<stopIndex)
        val arrivingList = list.slice(stopIndex + 1..<list.size)

        passedList.reversed().forEach {
            val closestTime = if (timeline.isEmpty()) time else timeline.last().time
            val nearestTime =
                if (workdays) it.workdays?.getNearestStop(closestTime, true)
                else it.weekends?.getNearestStop(closestTime, true)

            if (nearestTime != null) timeline += TimelineElement(
                it.name,
                nearestTime,
                TimelineElement.getState(nearestTime)
            )
        }

        timeline.reverse()
        timeline += TimelineElement(name, time, TimelineElement.getState(time))

        arrivingList.forEach {
            val closestTime = if (timeline.isEmpty()) time else timeline.last().time
            val nearestTime =
                if (workdays) it.workdays?.getNearestStop(closestTime, false)
                else it.weekends?.getNearestStop(closestTime, false)

            if (nearestTime != null) timeline += TimelineElement(
                it.name,
                nearestTime,
                TimelineElement.getState(nearestTime)
            )
        }

        return timeline
    }
}