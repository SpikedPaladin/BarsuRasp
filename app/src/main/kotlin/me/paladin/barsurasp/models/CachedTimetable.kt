package me.paladin.barsurasp.models

import kotlinx.serialization.Serializable

@Serializable
data class CachedTimetable(
    var timetables: List<Timetable>
)