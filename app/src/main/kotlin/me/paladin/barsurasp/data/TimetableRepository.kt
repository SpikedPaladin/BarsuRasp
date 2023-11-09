package me.paladin.barsurasp.data

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import me.paladin.barsurasp.App
import me.paladin.barsurasp.data.loaders.StudentLoader
import me.paladin.barsurasp.models.CachedTimetable
import me.paladin.barsurasp.models.Timetable
import java.io.File

object TimetableRepository {
    private const val CACHE_FOLDER = "app/groups"

    fun refreshTimetable(group: String) {
        val groupFile = File(getCacheFolder().path, "$group.json")

        groupFile.delete()
    }

    suspend fun getTimetable(group: String, date: String): Timetable? {
        var timetable: Timetable?
        val cacheFolder = getCacheFolder()

        if (!cacheFolder.exists())
            cacheFolder.mkdirs()

        val groupFile = File(cacheFolder.path, "$group.json")

        if (groupFile.exists()) {
            val cache = Json.decodeFromString<CachedTimetable>(groupFile.readText())

            timetable = cache.timetables.find { it.date == date }

            if (timetable == null) {
                timetable = StudentLoader.getTimetable(group, date)

                if (timetable != null) {
                    cache.timetables += timetable
                    groupFile.writeText(Json.encodeToString(cache))
                }
            }
        } else {
            timetable = StudentLoader.getTimetable(group, date)

            if (timetable != null)
                groupFile.writeText(Json.encodeToString(CachedTimetable(listOf(timetable))))
        }
        return timetable
    }

    private fun getCacheFolder() = File(App.getCacheDir(), CACHE_FOLDER)
}