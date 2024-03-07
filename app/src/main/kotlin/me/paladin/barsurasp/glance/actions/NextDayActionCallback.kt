package me.paladin.barsurasp.glance.actions

import android.content.Context
import androidx.glance.GlanceId
import androidx.glance.action.ActionParameters
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.state.updateAppWidgetState
import me.paladin.barsurasp.data.TimetableRepository
import me.paladin.barsurasp.glance.TimetableWidget
import me.paladin.barsurasp.glance.WidgetKeys
import me.paladin.barsurasp.models.Item
import me.paladin.barsurasp.utils.getNextApiDate
import me.paladin.barsurasp.utils.getWeekForDate

class NextDayActionCallback : ActionCallback {

    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        updateAppWidgetState(context, glanceId) { prefs ->
            val nextDay = getNextApiDate(prefs[WidgetKeys.date]!!)

            try {
                val timetable = TimetableRepository.getTimetable(
                    Item(prefs[WidgetKeys.group]!!),
                    getWeekForDate(nextDay)
                )

                if (timetable != null)
                    prefs[WidgetKeys.timetable] = timetable.encode()
                else
                    prefs[WidgetKeys.timetable] = ""

                prefs[WidgetKeys.networkError] = false
            } catch (_: Exception) {
                prefs[WidgetKeys.networkError] = true
                prefs[WidgetKeys.timetable] = ""
            }

            prefs[WidgetKeys.date] = nextDay
        }
        TimetableWidget().update(context, glanceId)
    }
}