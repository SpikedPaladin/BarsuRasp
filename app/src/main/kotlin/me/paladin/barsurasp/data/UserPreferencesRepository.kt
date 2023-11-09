package me.paladin.barsurasp.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import me.paladin.barsurasp.models.AppTheme

class UserPreferencesRepository(
    private val dataStore: DataStore<Preferences>
) {
    private object Keys {
        val week = stringPreferencesKey("week")
        val monet = booleanPreferencesKey("monet")
        val theme = stringPreferencesKey("theme")
        val mainGroup = stringPreferencesKey("mainGroup")
        val adsWatched = intPreferencesKey("adsWatched")
        val starredGroups = stringSetPreferencesKey("starredGroups")
    }

    private inline val Preferences.week
        get() = this[Keys.week] ?: "current"
    private inline val Preferences.monet
        get() = this[Keys.monet] ?: true
    private inline val Preferences.mainGroup
        get() = this[Keys.mainGroup] ?: ""
    private inline val Preferences.adsWatched
        get() = this[Keys.adsWatched] ?: 0
    private inline val Preferences.starredGroups
        get() = this[Keys.starredGroups]

    val week: Flow<String> = dataStore.data
        .map { preferences ->
            preferences.week
        }
        .distinctUntilChanged()

    suspend fun changeWeek(week: String) {
        dataStore.edit { it[Keys.week] = week }
    }

    val monet: Flow<Boolean> = dataStore.data
        .map { preferences ->
            preferences.monet
        }
        .distinctUntilChanged()

    suspend fun changeMonet(monet: Boolean) {
        dataStore.edit { it[Keys.monet] = monet }
    }

    val theme: Flow<AppTheme> = dataStore.data
        .map {
            when (it[Keys.theme]) {
                "day" -> AppTheme.DAY
                "night" -> AppTheme.NIGHT
                else -> AppTheme.AUTO
            }
        }
        .distinctUntilChanged()

    suspend fun changeTheme(theme: AppTheme) {
        dataStore.edit { it[Keys.theme] = when (theme) {
            AppTheme.DAY -> "day"
            AppTheme.NIGHT -> "night"
            AppTheme.AUTO -> "auto"
        }}
    }

    val mainGroup: Flow<String> = dataStore.data
        .map { preferences ->
            preferences.mainGroup
        }
        .distinctUntilChanged()

    suspend fun changeMainGroup(group: String) {
        dataStore.edit { it[Keys.mainGroup] = group }
    }

    val adsWatched: Flow<Int> = dataStore.data
        .map { preferences ->
            preferences.adsWatched
        }
        .distinctUntilChanged()

    suspend fun incrementAdCounter() {
        dataStore.edit { it[Keys.adsWatched] = it[Keys.adsWatched]?.plus(1) ?: (0 + 1) }
    }

    val starredGroups: Flow<Set<String>?> = dataStore.data
        .map { preferences ->
            preferences.starredGroups
        }
        .distinctUntilChanged()

    suspend fun starGroup(group: String) {
        dataStore.edit {
            if (it[Keys.starredGroups] == null)
                it[Keys.starredGroups] = setOf()

            it[Keys.starredGroups] = it[Keys.starredGroups]!! + group
        }
    }
}