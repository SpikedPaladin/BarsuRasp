package me.paladin.barsurasp.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import me.paladin.barsurasp.models.AppTheme
import me.paladin.barsurasp.models.BusPath

class UserPreferencesRepository(
    private val dataStore: DataStore<Preferences>
) {
    private object Keys {
        val monet = booleanPreferencesKey("monet")
        val theme = stringPreferencesKey("theme")
        val buses = stringPreferencesKey("buses")
        val showBuses = booleanPreferencesKey("showBuses")
        val selectedPath = intPreferencesKey("selectedPath")
        val mainGroup = stringPreferencesKey("mainGroup")
        val adsWatched = intPreferencesKey("adsWatched")
        val savedItems = stringSetPreferencesKey("savedItems")
    }

    private inline val Preferences.monet
        get() = this[Keys.monet] ?: true
    private inline val Preferences.buses
        get() = this[Keys.buses] ?: ""
    private inline val Preferences.showBuses
        get() = this[Keys.showBuses] ?: true
    private inline val Preferences.selectedPath
        get() = this[Keys.selectedPath] ?: -1
    private inline val Preferences.mainGroup
        get() = this[Keys.mainGroup] ?: ""
    private inline val Preferences.adsWatched
        get() = this[Keys.adsWatched] ?: 0
    private inline val Preferences.savedItems
        get() = this[Keys.savedItems] ?: setOf()

    val monet = dataStore.data
        .map { preferences ->
            preferences.monet
        }.distinctUntilChanged()

    suspend fun changeMonet(monet: Boolean) {
        dataStore.edit { it[Keys.monet] = monet }
    }

    val theme = dataStore.data
        .map {
            AppTheme.fromPref(it[Keys.theme])
        }.distinctUntilChanged()

    suspend fun changeTheme(theme: AppTheme) {
        dataStore.edit {
            it[Keys.theme] = theme.toPref()
        }
    }

    val mainGroup = dataStore.data
        .map { preferences ->
            val group = preferences.mainGroup
            if (group.contains(":"))
                group
            else
                "$group:?"
        }.distinctUntilChanged()

    suspend fun changeMainGroup(group: String) {
        dataStore.edit { it[Keys.mainGroup] = group }
    }

    val adsWatched = dataStore.data
        .map { preferences ->
            preferences.adsWatched
        }.distinctUntilChanged()

    suspend fun incrementAdCounter() {
        dataStore.edit { it[Keys.adsWatched] = it.adsWatched + 1 }
    }

    val buses = dataStore.data
        .map {
            val serialized = it.buses
            if (serialized != "") {
                Json.decodeFromString<BusPath.Wrapper>(serialized).paths
            } else
                listOf()
        }.distinctUntilChanged()

    val selectedPath = dataStore.data
        .map { it.selectedPath }.distinctUntilChanged()

    suspend fun selectPath(index: Int) {
        dataStore.edit { it[Keys.selectedPath] = index }
    }

    suspend fun addPath(path: BusPath) {
        dataStore.edit {
            val buses = if (it.buses != "") Json.decodeFromString<BusPath.Wrapper>(it.buses) else BusPath.Wrapper()
            buses.paths += path

            if (buses.paths.size == 1)
                it[Keys.selectedPath] = 0

            it[Keys.buses] = Json.encodeToString(buses)
        }
    }

    suspend fun deletePath(index: Int) {
        dataStore.edit {
            val buses = Json.decodeFromString<BusPath.Wrapper>(it.buses)
            val newList = buses.paths.toMutableList()
            newList.removeAt(index)
            buses.paths = newList

            if (buses.paths.isEmpty())
                it[Keys.selectedPath] = -1
            else if (buses.paths.size < it.selectedPath + 1)
                it[Keys.selectedPath] = buses.paths.size - 1

            it[Keys.buses] = Json.encodeToString(buses)
        }
    }

    val showBuses = dataStore.data
        .map {
            it.showBuses
        }.distinctUntilChanged()

    suspend fun setShowBuses(enabled: Boolean) {
        dataStore.edit { it[Keys.showBuses] = enabled }
    }

    val savedItems = dataStore.data
        .map { preferences ->
            preferences.savedItems
        }.distinctUntilChanged()

    suspend fun saveItem(item: String) {
        dataStore.edit {
            if (it[Keys.savedItems] == null)
                it[Keys.savedItems] = setOf()

            val itemName = item.split(":", limit = 2)[0]
            if (it[Keys.savedItems]!!.firstOrNull { predicate -> predicate.startsWith(itemName) } != null)
                it[Keys.savedItems] = it[Keys.savedItems]!!.toMutableSet() - item
            else
                it[Keys.savedItems] = it[Keys.savedItems]!! + item
        }
    }
}