package me.paladin.barsurasp.models

import me.paladin.barsurasp.utils.getCurrentHour
import me.paladin.barsurasp.utils.getCurrentMinute

data class TimelineElement(
    val name: String,
    val time: BusStop.Time,
    val state: State
) {



    enum class State {
        PASSED,
        ARRIVING,
        INCOMING
    }

    companion object {

        fun getState(time: BusStop.Time): State {
            val hour = getCurrentHour()
            val minute = getCurrentMinute()
            return if (hour == time.hour && minute == time.minute)
                State.ARRIVING
            else if (hour < time.hour ||(hour == time.hour && minute <= time.minute))
                State.INCOMING
            else
                State.PASSED
        }
    }
}