package me.paladin.barsurasp.data.loaders

import me.paladin.barsurasp.models.Faculty
import me.paladin.barsurasp.models.Speciality
import me.paladin.barsurasp.utils.getStudentPage
import org.jsoup.Jsoup

object GroupLoader {

    suspend fun getFaculties(): List<Faculty> {
        val page = getStudentPage()
        val doc = Jsoup.parse(page)

        val faculties = mutableListOf<Faculty>()
        val facultyForm = doc.getElementById("faculty")
        val specialityForm = doc.getElementById("speciality")
        val groupForm = doc.getElementById("groups")
        facultyForm?.getElementsByTag("option")?.forEach { facultyElement ->
            val faculty = facultyElement.attr("value")

            if (faculty != "selectcard") {
                val specialities = mutableListOf<Speciality>()

                specialityForm?.getElementsByTag("option")?.forEach { specialityElement ->
                    val speciality = specialityElement.attr("value")

                    if (speciality != "selectcard" && specialityElement.attr("class") == faculty) {
                        val groups = mutableListOf<String>()

                        groupForm?.getElementsByTag("option")?.forEach { groupElement ->
                            val group = groupElement.attr("value")

                            if (group != "selectcard" && groupElement.attr("class") == speciality)
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
}