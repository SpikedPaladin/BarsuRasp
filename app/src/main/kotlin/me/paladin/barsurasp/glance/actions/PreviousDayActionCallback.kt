package me.paladin.barsurasp.glance.actions

import android.content.Context
import android.util.Log
import androidx.glance.GlanceId
import androidx.glance.action.ActionParameters
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.state.updateAppWidgetState
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import me.paladin.barsurasp.data.TimetableRepository
import me.paladin.barsurasp.glance.TimetableWidget
import me.paladin.barsurasp.glance.WidgetKeys
import me.paladin.barsurasp.models.Timetable
import me.paladin.barsurasp.utils.getCurrentWeek
import me.paladin.barsurasp.utils.getCurrentWeekEnd
import me.paladin.barsurasp.utils.getPrevApiDate

class PreviousDayActionCallback : ActionCallback {

    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        updateAppWidgetState(context, glanceId) { prefs ->
            if (prefs[WidgetKeys.Prefs.date]!! == getCurrentWeek("dd.MM"))
                return@updateAppWidgetState

            val prevDay = getPrevApiDate(prefs[WidgetKeys.Prefs.date]!!)

            if (prefs[WidgetKeys.Prefs.timetable] != null && prefs[WidgetKeys.Prefs.group] == null)
                prefs[WidgetKeys.Prefs.group] = (Json.decodeFromString(prefs[WidgetKeys.Prefs.timetable]!!) as Timetable).group

            Log.i("", "onAction: $prevDay, ${getCurrentWeekEnd("dd.MM")}")
            if (prevDay == getCurrentWeekEnd("dd.MM") || prefs[WidgetKeys.Prefs.networkError] == true) {
                try {
                    val timetable = TimetableRepository.getTimetable(
                        prefs[WidgetKeys.Prefs.group]!!,
                        getCurrentWeek()
                    )
                    if (timetable != null)
                        prefs[WidgetKeys.Prefs.timetable] = Json.encodeToString(timetable)
                    else  prefs[WidgetKeys.Prefs.timetable] = ""

                    prefs[WidgetKeys.Prefs.networkError] = false
                } catch (_: Exception) {
                    prefs[WidgetKeys.Prefs.networkError] = true
                    prefs[WidgetKeys.Prefs.timetable] = ""
                }
            }

            prefs[WidgetKeys.Prefs.date] = prevDay
        }
        TimetableWidget().update(context, glanceId)
    }
}