package me.paladin.barsurasp.glance

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

object WidgetKeys {
    val date = stringPreferencesKey("widgetDate")
    val group = stringPreferencesKey("widgetGroup")
    val timetable = stringPreferencesKey("widgetTimetable")
    val networkError = booleanPreferencesKey("widgetNetworkError")
}