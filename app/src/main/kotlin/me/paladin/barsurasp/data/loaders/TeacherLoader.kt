package me.paladin.barsurasp.data.loaders

import me.paladin.barsurasp.models.Department
import me.paladin.barsurasp.utils.getTeacherPage
import org.jsoup.Jsoup

object TeacherLoader {

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