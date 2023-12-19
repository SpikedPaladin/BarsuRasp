package me.paladin.barsurasp.glance.actions

import android.content.Context
import androidx.glance.GlanceId
import androidx.glance.action.ActionParameters
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.state.updateAppWidgetState
import me.paladin.barsurasp.data.TimetableRepository
import me.paladin.barsurasp.glance.TimetableWidget
import me.paladin.barsurasp.glance.WidgetKeys
import me.paladin.barsurasp.utils.getCurrentWeek
import me.paladin.barsurasp.utils.getPrevApiDate
import me.paladin.barsurasp.utils.getWeekForDate

class PreviousDayActionCallback : ActionCallback {

    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        updateAppWidgetState(context, glanceId) { prefs ->
            if (prefs[WidgetKeys.date]!! == getCurrentWeek("dd.MM"))
                return@updateAppWidgetState

            val prevDay = getPrevApiDate(prefs[WidgetKeys.date]!!)

            try {
                val timetable = TimetableRepository.getTimetable(
                    prefs[WidgetKeys.group]!!,
                    getWeekForDate(prevDay)
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

            prefs[WidgetKeys.date] = prevDay
        }
        TimetableWidget().update(context, glanceId)
    }
}