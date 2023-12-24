package me.paladin.barsurasp.ui

import android.appwidget.AppWidgetManager
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import me.paladin.barsurasp.data.TimetableRepository
import me.paladin.barsurasp.glance.TimetableWidget
import me.paladin.barsurasp.glance.WidgetKeys
import me.paladin.barsurasp.ui.screens.ItemsScreen
import me.paladin.barsurasp.ui.theme.BarsuRaspTheme
import me.paladin.barsurasp.ui.viewmodels.SettingsViewModel
import me.paladin.barsurasp.utils.getCurrentApiDate
import me.paladin.barsurasp.utils.getCurrentWeek

class ConfigWidgetActivity : ComponentActivity() {
    private val viewModel by viewModels<SettingsViewModel>()
    private var widgetId = AppWidgetManager.INVALID_APPWIDGET_ID
    private val result = Intent()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupActivity()
        setContent {
            val theme by viewModel.theme.collectAsState()
            val monet by viewModel.monet.collectAsState()

            BarsuRaspTheme(theme.isDark(), monet) {
                ItemsScreen(itemSelected = { it, _ -> handleSelectGroup(it) })
            }
        }
    }

    private fun handleSelectGroup(group: String) {
        setResult(RESULT_OK, result)
        saveWidgetState(group)
    }

    private fun saveWidgetState(group: String) = lifecycleScope.launch(Dispatchers.IO) {
        val glanceId = GlanceAppWidgetManager(applicationContext).getGlanceIdBy(widgetId)
        val timetable = TimetableRepository.getTimetable(group, getCurrentWeek())

        updateAppWidgetState(applicationContext, glanceId) { prefs ->
            prefs[WidgetKeys.date] = getCurrentApiDate()
            prefs[WidgetKeys.group] = group
            Log.i("Widget", "saveWidgetState: $group")
            timetable?.let {
                prefs[WidgetKeys.timetable] = it.encode()
            }
        }
        TimetableWidget().update(applicationContext, glanceId)
        finish()
    }

    private fun setupActivity() {
        setResult(RESULT_CANCELED, result)
        getWidgetId()
        initResult()
    }

    private fun getWidgetId() {
        widgetId = intent.extras?.getInt(
            AppWidgetManager.EXTRA_APPWIDGET_ID,
            AppWidgetManager.INVALID_APPWIDGET_ID
        ) ?: return
        if (widgetId == AppWidgetManager.INVALID_APPWIDGET_ID) finish()
    }

    private fun initResult() = result.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId)
}