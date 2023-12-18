package me.paladin.barsurasp.utils

import io.ktor.client.call.body
import io.ktor.client.request.forms.submitForm
import io.ktor.client.request.get
import io.ktor.http.Parameters
import me.paladin.barsurasp.App

private const val STUDENT_URL = "https://rasp.barsu.by/stud.php"
private const val TEACHER_URL = "https://rasp.barsu.by/teach.php"

suspend fun getStudentPage(group: String? = null, date: String? = null): String {
    if (group != null && date != null) {
        val params = Parameters.build {
            append("faculty", "selectcard")
            append("speciality", "selectcard")
            append("groups", group)
            append("weekbegindate", date)
        }

        return App.client.submitForm(STUDENT_URL, params).body()
    }

    return App.client.get(STUDENT_URL).body()
}

suspend fun getTeacherPage(name: String? = null, date: String? = null): String {
    if (name != null && date != null) {
        val params = Parameters.build {
            append("kafedra", "selectcard")
            append("teacher", name)
            append("weekbegindate", date)
        }

        return App.client.submitForm(TEACHER_URL, params).body()
    }

    return App.client.get(TEACHER_URL).body()
}

fun isGroup(item: String) = item.contains("[0-9]".toRegex())