package me.paladin.barsurasp.data

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import me.paladin.barsurasp.App
import me.paladin.barsurasp.data.loaders.GroupLoader
import me.paladin.barsurasp.data.loaders.TeacherLoader
import me.paladin.barsurasp.models.Timetable
import me.paladin.barsurasp.utils.isGroup
import java.io.File

object TimetableRepository {
    private const val GROUPS_CACHE_FOLDER = "app/groups"
    private const val TEACHERS_CACHE_FOLDER = "app/teachers"

    fun refreshTimetable(item: String) {
        val itemFolder = File(getCacheFolder(item).path, "$item.json")

        itemFolder.delete()
    }

    suspend fun getTimetable(item: String, date: String): Timetable? {
        return if (isGroup(item))
            getGroupTimetable(item, date)
        else getTeacherTimetable(item, date)
    }

    private suspend fun getGroupTimetable(group: String, date: String): Timetable.Group? {
        var timetable: Timetable.Group?
        val cacheFolder = getGroupsCacheFolder()

        if (!cacheFolder.exists())
            cacheFolder.mkdirs()

        val groupFile = File(cacheFolder.path, "$group.json")

        if (groupFile.exists()) {
            val cache = Json.decodeFromString<Timetable.Group.Wrapper>(groupFile.readText())

            timetable = cache.timetables.find { it.date == date }

            if (timetable == null) {
                timetable = GroupLoader.getTimetable(group, date)

                if (timetable != null) {
                    cache.timetables += timetable
                    groupFile.writeText(Json.encodeToString(cache))
                }
            }
        } else {
            timetable = GroupLoader.getTimetable(group, date)

            if (timetable != null)
                groupFile.writeText(Json.encodeToString(Timetable.Group.Wrapper(listOf(timetable))))
        }
        return timetable
    }

    private suspend fun getTeacherTimetable(name: String, date: String): Timetable.Teacher? {
        var timetable: Timetable.Teacher?
        val cacheFolder = getTeachersCacheFolder()

        if (!cacheFolder.exists())
            cacheFolder.mkdirs()

        val groupFile = File(cacheFolder.path, "$name.json")

        if (groupFile.exists()) {
            val cache = Json.decodeFromString<Timetable.Teacher.Wrapper>(groupFile.readText())

            timetable = cache.timetables.find { it.date == date }

            if (timetable == null) {
                timetable = TeacherLoader.getTimetable(name, date)

                if (timetable != null) {
                    cache.timetables += timetable
                    groupFile.writeText(Json.encodeToString(cache))
                }
            }
        } else {
            timetable = TeacherLoader.getTimetable(name, date)

            if (timetable != null)
                groupFile.writeText(Json.encodeToString(Timetable.Teacher.Wrapper(listOf(timetable))))
        }
        return timetable
    }

    private fun getCacheFolder(item: String) = if (isGroup(item))
        getGroupsCacheFolder()
    else
        getTeachersCacheFolder()

    private fun getGroupsCacheFolder() = File(App.getCacheDir(), GROUPS_CACHE_FOLDER)
    private fun getTeachersCacheFolder() = File(App.getCacheDir(), TEACHERS_CACHE_FOLDER)
}