package me.paladin.barsurasp.glance

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.datastore.preferences.core.Preferences
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.LocalContext
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.appWidgetBackground
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.currentState
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.padding
import androidx.glance.state.GlanceStateDefinition
import androidx.glance.state.PreferencesGlanceStateDefinition
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import kotlinx.serialization.json.Json
import me.paladin.barsurasp.R
import me.paladin.barsurasp.glance.components.GlanceTimetableHeader
import me.paladin.barsurasp.glance.components.GlanceTimetableList
import me.paladin.barsurasp.models.Timetable
import me.paladin.barsurasp.utils.getCurrentApiDate

class TimetableWidget : GlanceAppWidget() {
    override var stateDefinition: GlanceStateDefinition<*> = PreferencesGlanceStateDefinition

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            val prefs = currentState<Preferences>()

            Content(prefs)
        }
    }

    @Composable
    private fun Content(prefs: Preferences) {
        Column(
            verticalAlignment = Alignment.Top,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = GlanceModifier
                .fillMaxSize()
                .appWidgetBackground()
                .background(GlanceTheme.colors.background)
        ) {
            GlanceTimetableHeader(prefs[WidgetKeys.Prefs.group] ?: LocalContext.current.getString(R.string.widget_error), prefs[WidgetKeys.Prefs.date] ?: getCurrentApiDate())

            if (prefs[WidgetKeys.Prefs.networkError] == true) {
                Text(text = "Ошибка подключения к сети", modifier = GlanceModifier.padding(12.dp), style = TextStyle(color = GlanceTheme.colors.onBackground))
                return@Column
            }

            val jsonTimetable = prefs[WidgetKeys.Prefs.timetable]
            if (jsonTimetable == null || jsonTimetable == "") {
                Text(text = LocalContext.current.getString(R.string.widget_error_no_timetable), modifier = GlanceModifier.padding(12.dp), style = TextStyle(color = GlanceTheme.colors.onBackground))
                return@Column
            }
            val timetable: Timetable = Json.decodeFromString(prefs[WidgetKeys.Prefs.timetable]!!)

            val day = timetable.getDayFromDate(prefs[WidgetKeys.Prefs.date] ?: getCurrentApiDate())
            if (day != null)
                GlanceTimetableList(day)
            else {
                Text(text = LocalContext.current.getString(R.string.widget_no_lessons), modifier = GlanceModifier.padding(12.dp), style = TextStyle(color = GlanceTheme.colors.onBackground))
            }
        }
    }
}