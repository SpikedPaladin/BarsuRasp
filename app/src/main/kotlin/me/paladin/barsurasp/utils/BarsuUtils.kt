package me.paladin.barsurasp.utils

import io.ktor.client.call.body
import io.ktor.client.request.forms.submitForm
import io.ktor.client.request.get
import io.ktor.http.Parameters
import me.paladin.barsurasp.App

private const val BASE_URL = "https://rasp.barsu.by/stud.php"

suspend fun getTimetablePage(group: String? = null, date: String? = null): String {
    if (group != null && date != null) {
        val params = Parameters.build {
            append("faculty", "selectcard")
            append("speciality", "selectcard")
            append("groups", group)
            append("weekbegindate", date)
        }

        return App.client.submitForm(BASE_URL, params).body()
    }

    return App.client.get(BASE_URL).body()
}