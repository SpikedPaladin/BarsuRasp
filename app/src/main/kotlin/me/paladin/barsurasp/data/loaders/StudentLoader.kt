package me.paladin.barsurasp.data.loaders

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.forms.submitForm
import io.ktor.client.request.get
import io.ktor.http.Parameters
import me.paladin.barsurasp.models.DaySchedule
import me.paladin.barsurasp.models.Faculty
import me.paladin.barsurasp.models.Lesson
import me.paladin.barsurasp.models.Speciality
import me.paladin.barsurasp.models.Sublesson
import me.paladin.barsurasp.models.Timetable
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import org.jsoup.nodes.TextNode

object StudentLoader {
    private const val BASE_URL = "https://rasp.barsu.by/stud.php"
    private val client = HttpClient()

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
            if (table[i].hasAttr("rowspan")) {
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

    suspend fun getFaculties(): List<Faculty> {
        val page = getTimetablePage()
        val doc = Jsoup.parse(page)

        val faculties = mutableListOf<Faculty>()
        val facultyForm = doc.getElementById("faculty")
        val specialityForm = doc.getElementById("speciality")
        val groupForm = doc.getElementById("groups")
        facultyForm?.getElementsByTag("option")?.forEach {
            val faculty = it.attr("value")

            if (faculty != "selectcard") {
                val specialities = mutableListOf<Speciality>()

                specialityForm?.getElementsByTag("option")?.forEach {
                    val speciality = it.attr("value")

                    if (speciality != "selectcard" && it.attr("class") == faculty) {
                        val groups = mutableListOf<String>()

                        groupForm?.getElementsByTag("option")?.forEach {
                            val group = it.attr("value")

                            if (group != "selectcard" && it.attr("class") == speciality)
                                groups += group
                        }

                        specialities += Speciality(speciality, groups)
                    }
                }

                faculties += Faculty(faculty, specialities)
            }
        }

        return faculties
    }

    private suspend fun getTimetablePage(group: String? = null, date: String? = null): String {
        if (group != null && date != null) {
            val params = Parameters.build {
                append("faculty", "selectcard")
                append("speciality", "selectcard")
                append("groups", group)
                append("weekbegindate", date)
            }

            return client.submitForm(BASE_URL, params).body()
        }

        return client.get(BASE_URL).body()
    }

    private fun Element.splitString(): List<String> {
        var string = ""

        this.childNodes().forEach {
            string += if (it is TextNode)
                it.wholeText.trim()
            else
                "|"
        }

        if (string.endsWith("|"))
            string = string.substring(0, string.length - 1)

        return string.split("|")
    }
}