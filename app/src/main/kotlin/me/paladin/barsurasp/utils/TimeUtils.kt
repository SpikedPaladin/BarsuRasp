package me.paladin.barsurasp.utils

import me.paladin.barsurasp.R
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Calendar.DAY_OF_MONTH
import java.util.Calendar.DAY_OF_WEEK
import java.util.Calendar.DAY_OF_YEAR
import java.util.Calendar.FRIDAY
import java.util.Calendar.HOUR_OF_DAY
import java.util.Calendar.MILLISECOND
import java.util.Calendar.MINUTE
import java.util.Calendar.MONDAY
import java.util.Calendar.MONTH
import java.util.Calendar.SATURDAY
import java.util.Calendar.SECOND
import java.util.Calendar.THURSDAY
import java.util.Calendar.TUESDAY
import java.util.Calendar.WEDNESDAY
import java.util.Calendar.WEEK_OF_YEAR
import java.util.Locale

fun readableDay(apiDay: String) = when (apiDay) {
    "ПН" -> R.string.monday
    "ВТ" -> R.string.tuesday
    "СР" -> R.string.wednesday
    "ЧТ" -> R.string.thursday
    "ПТ" -> R.string.friday
    "СБ" -> R.string.saturday
    "ВС" -> R.string.sunday
    else -> R.string.unknown
}

fun getApiDay(date: String): String {
    Calendar.getInstance().apply {
        set(DAY_OF_MONTH, date.split(".")[0].toInt())
        set(MONTH, date.split(".")[1].toInt() - 1)
        
        return when (get(DAY_OF_WEEK)) {
            MONDAY -> "ПН"
            TUESDAY -> "ВТ"
            WEDNESDAY -> "СР"
            THURSDAY -> "ЧТ"
            FRIDAY -> "ПТ"
            SATURDAY -> "СБ"
            else -> "ВС"
        }
    }
}

fun getCurrentApiDate(): String {
    return SimpleDateFormat("dd.MM", Locale.US)
        .format(Calendar.getInstance().time)
}

fun getWeekForDate(date: String): String {
    Calendar.getInstance().apply {
        firstDayOfWeek = MONDAY
        set(DAY_OF_MONTH, date.split(".")[0].toInt())
        set(MONTH, date.split(".")[1].toInt() - 1)

        // Какая-то хуета непонятная но без этого не работает
        time

        set(DAY_OF_WEEK, MONDAY)
        set(HOUR_OF_DAY, 0)
        set(MINUTE, 0)
        set(SECOND, 0)
        set(MILLISECOND, 0)

        return SimpleDateFormat("yyyy-MM-dd", Locale.US).format(time)
    }
}

fun getNextApiDate(date: String): String {
    Calendar.getInstance().apply {
        firstDayOfWeek = MONDAY
        set(DAY_OF_MONTH, date.split(".")[0].toInt())
        set(MONTH, date.split(".")[1].toInt() - 1)
        add(DAY_OF_YEAR, 1)
        
        return SimpleDateFormat("dd.MM", Locale.US).format(time)
    }
}

fun getPrevApiDate(date: String): String {
    Calendar.getInstance().apply {
        firstDayOfWeek = MONDAY
        set(DAY_OF_MONTH, date.split(".")[0].toInt())
        set(MONTH, date.split(".")[1].toInt() - 1)
        add(DAY_OF_YEAR, -1)

        return SimpleDateFormat("dd.MM", Locale.US).format(time)
    }
}

fun getCurrentWeek(format: String = "yyyy-MM-dd"): String {
    Calendar.getInstance().apply {
        firstDayOfWeek = MONDAY
        set(DAY_OF_WEEK, MONDAY)
        set(HOUR_OF_DAY, 0)
        set(MINUTE, 0)
        set(SECOND, 0)
        set(MILLISECOND, 0)
        
        return SimpleDateFormat(format, Locale.US).format(time)
    }
}

fun getNextWeek(format: String = "yyyy-MM-dd"): String {
    Calendar.getInstance().apply {
        firstDayOfWeek = MONDAY
        set(DAY_OF_WEEK, MONDAY)
        set(HOUR_OF_DAY, 0)
        set(MINUTE, 0)
        set(SECOND, 0)
        set(MILLISECOND, 0)
        add(WEEK_OF_YEAR, 1)
        
        return SimpleDateFormat(format, Locale.US).format(time)
    }
}