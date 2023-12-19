package me.paladin.barsurasp.glance.components

import androidx.compose.runtime.Composable
import androidx.glance.appwidget.lazy.LazyColumn
import androidx.glance.appwidget.lazy.itemsIndexed
import me.paladin.barsurasp.models.DaySchedule

@Composable
fun GlanceTimetableList(day: DaySchedule) {
    LazyColumn {
        if (day.actualLessons != null) itemsIndexed(day.actualLessons!!) { index, item ->
            GlanceLessonItem(item, index < day.actualLessons!!.size - 1)
        }
    }
}