package me.paladin.barsurasp.data

import me.paladin.barsurasp.App
import me.paladin.barsurasp.data.loaders.GroupLoader
import me.paladin.barsurasp.data.loaders.TeacherLoader
import me.paladin.barsurasp.models.Item
import me.paladin.barsurasp.models.Timetable
import me.paladin.barsurasp.utils.loadFromFile
import me.paladin.barsurasp.utils.saveToFile
import java.io.File

object TimetableRepository {
    private const val GROUPS_CACHE_FOLDER = "app/groups"
    private const val TEACHERS_CACHE_FOLDER = "app/teachers"

    fun refreshTimetable(item: Item) {
        File(getCacheFolder(item).path, "${item.title}.json").delete()
    }

    suspend fun getTimetable(item: Item, date: String): Timetable? {
        return if (item.isGroup())
            getGroupTimetable(item, date)
        else getTeacherTimetable(item, date)
    }

    private suspend fun getGroupTimetable(item: Item, date: String): Timetable.Group? {
        val group = item.title
        var timetable: Timetable.Group?
        val cacheFolder = getGroupsCacheFolder()

        if (!cacheFolder.exists())
            cacheFolder.mkdirs()

        val groupFile = File(cacheFolder.path, "$group.json")

        if (groupFile.exists()) {
            val cache = loadFromFile<Timetable.Group.Wrapper>(groupFile)

            timetable = cache.timetables.find { it.date == date }

            if (timetable == null) {
                timetable = GroupLoader.getTimetable(group, date)

                if (timetable != null) {
                    cache.timetables += timetable
                    saveToFile(groupFile, cache)
                }
            }
        } else {
            timetable = GroupLoader.getTimetable(group, date)

            if (timetable != null)
                saveToFile(groupFile, Timetable.Group.Wrapper(listOf(timetable)))
        }
        return timetable
    }

    private suspend fun getTeacherTimetable(item: Item, date: String): Timetable.Teacher? {
        val name = item.title
        var timetable: Timetable.Teacher?
        val cacheFolder = getTeachersCacheFolder()

        if (!cacheFolder.exists())
            cacheFolder.mkdirs()

        val teacherFile = File(cacheFolder.path, "$name.json")

        if (teacherFile.exists()) {
            val cache = loadFromFile<Timetable.Teacher.Wrapper>(teacherFile)

            timetable = cache.timetables.find { it.date == date }

            if (timetable == null) {
                timetable = TeacherLoader.getTimetable(name, date)

                if (timetable != null) {
                    cache.timetables += timetable
                    saveToFile(teacherFile, cache)
                }
            }
        } else {
            timetable = TeacherLoader.getTimetable(name, date)

            if (timetable != null)
                saveToFile(teacherFile, Timetable.Teacher.Wrapper(listOf(timetable)))
        }
        return timetable
    }

    private fun getCacheFolder(item: Item) = if (item.isGroup())
        getGroupsCacheFolder()
    else
        getTeachersCacheFolder()

    private fun getGroupsCacheFolder() = File(App.getCacheDir(), GROUPS_CACHE_FOLDER)
    private fun getTeachersCacheFolder() = File(App.getCacheDir(), TEACHERS_CACHE_FOLDER)
}