package me.paladin.barsurasp.data.loaders

import me.paladin.barsurasp.models.DaySchedule
import me.paladin.barsurasp.models.Department
import me.paladin.barsurasp.models.Lesson
import me.paladin.barsurasp.models.Timetable
import me.paladin.barsurasp.utils.getTeacherPage
import org.jsoup.Jsoup

object TeacherLoader {

    suspend fun getTimetable(name: String, week: String): Timetable.Teacher? {
        val page = getTeacherPage(name, week)
        val doc = Jsoup.parse(page)

        val rows = doc
            .getElementsByClass("table-bordered")
            .getOrNull(0)
            ?.getElementsByTag("tbody")
            ?.getOrNull(0)
            ?.getElementsByTag("tr")

        if (rows.isNullOrEmpty())
            return null

        val lastUpdate = doc.getElementsByClass("container")[2]
            .getElementsByTag("p")[0]
            .text()

        val days = mutableListOf<DaySchedule.Teacher>()
        var lessons = mutableListOf<Lesson.Teacher>()
        var schedule = DaySchedule.Teacher("", "")

        for (row in rows) {
            if (row.childNodes().isEmpty())
                continue

            val lessonInfo = row.getElementsByTag("td")
            var offset = 0

            if (lessonInfo.size > 4) {
                lessons = mutableListOf()
                schedule = DaySchedule.Teacher(
                    dayOfWeek = lessonInfo[offset++].text().trim(),
                    date = lessonInfo[offset++].text().trim()
                )
            }

            lessons += if (lessonInfo[offset + 1].text().trim() == "") {
                Lesson.Teacher(time = lessonInfo[offset++].text().trim())
            } else {
                Lesson.Teacher(time = lessonInfo[offset++].text().trim()).apply {
                    val rawName = lessonInfo[offset++].text().trim()

                    this.name = rawName.substring(0, rawName.lastIndexOf("-")).trim()
                    type = rawName.substring(rawName.lastIndexOf("-") + 1).trim()
                    groups = lessonInfo[offset++].text().trim()
                    place = lessonInfo[offset++].text().trim()
                    replaced = row.attr("bgcolor") == "#ffb2b9"
                }
            }

            if (lessonInfo[0].text().trim() == "19.35-21.00") {
                schedule.lessons = lessons
                days += schedule
            }
        }

        return Timetable.Teacher(
            lastUpdate = lastUpdate,
            days = days,
            name = name,
            date = week
        )
    }

    suspend fun getDepartments(): List<Department> {
        val page = getTeacherPage()
        val doc = Jsoup.parse(page)

        val departments = mutableListOf<Department>()
        val departmentForm = doc.getElementById("kafedra")
        val teacherForm = doc.getElementById("teacher")
        departmentForm?.getElementsByTag("option")?.forEach { departmentElement ->
            var department = departmentElement.attr("value")

            if (department != "selectcard" && department != "") {
                val teachers = mutableListOf<String>()

                if (department.trim() == "")
                    department = "Без кафедры"

                teacherForm?.getElementsByTag("option")?.forEach { teacherElement ->
                    val teacherName = teacherElement.attr("value")
                    var teacherDepartment = teacherElement.attr("class")

                    if (teacherDepartment.trim() == "")
                        teacherDepartment = "Без кафедры"

                    if (teacherName != "selectcard" && teacherName != "" && teacherDepartment == department)
                        teachers += teacherName
                }

                departments += Department(department, teachers)
            }
        }

        return departments
    }
}