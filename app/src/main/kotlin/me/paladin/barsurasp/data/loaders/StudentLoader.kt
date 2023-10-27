package me.paladin.barsurasp.data.loaders

import me.paladin.barsurasp.models.DaySchedule
import me.paladin.barsurasp.models.Lesson
import me.paladin.barsurasp.models.Sublesson
import me.paladin.barsurasp.models.Timetable
import me.paladin.barsurasp.utils.getTimetablePage
import me.paladin.barsurasp.utils.splitString
import org.jsoup.Jsoup

object StudentLoader {

    suspend fun getTimetable(group: String, week: String): Timetable? {
        val page = getTimetablePage(group, week)
        val doc = Jsoup.parse(page)

        val table = doc.getElementsByClass("min-p")[0]
            .getElementsByTag("td")

        if (table.size == 0)
            return null

        val lastUpdate = doc.getElementsByClass("container")[3]
            .getElementsByTag("p")[0]
            .text()

        val days = mutableListOf<DaySchedule>()
        var lessons = mutableListOf<Lesson>()
        for (i in table.indices step 2) {
            if (table[i].hasAttr("rows pan")) {
                val day = table[i].text().replace(" ", "")
                val date = table[i + 1].text().replace(" ", "")

                days += DaySchedule(
                    date = date,
                    dayOfWeek = day
                )

                lessons = mutableListOf()
                continue
            }

            val time = table[i].text().replace(" ", "")
            val rawLesson = table[i + 1].splitString()
            if (rawLesson.size > 1) {
                val sublessons = mutableListOf<Sublesson>()

                rawLesson.chunked(3).forEach {
                    var place: String? = null
                    var subgroup: String? = null

                    val rawPlace = it[2]
                    if (rawPlace.isNotEmpty()) {
                        if (rawPlace.contains(":")) {
                            val parsedPlace = rawPlace.split("  ")

                            place = parsedPlace[0]
                            subgroup = parsedPlace[1]
                        } else
                            place = it[2]
                    }

                    val rawName = it[0].split(" - ")
                    sublessons += Sublesson(
                        name = rawName[0],
                        type = rawName[1],
                        teacher = it[1],
                        place = place,
                        subgroup = subgroup
                    )
                }

                lessons += Lesson(
                    time = time,
                    sublessons = sublessons.sortedWith(compareBy { it.subgroup }),
                    replaced = table[i + 1].attr("bgcolor") == "#ffb2b9"
                )
            } else
                lessons += Lesson(time = time)

            if (time.startsWith("19"))
                days.last().lessons = lessons
        }

        return Timetable(
            lastUpdate = lastUpdate,
            days = days,
            group = group,
            date = week
        )
    }
}