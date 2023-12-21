package me.paladin.barsurasp.utils

import me.paladin.barsurasp.R
import me.paladin.barsurasp.models.BusStop
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
import java.util.Calendar.SUNDAY
import java.util.Calendar.THURSDAY
import java.util.Calendar.TUESDAY
import java.util.Calendar.WEDNESDAY
import java.util.Calendar.WEEK_OF_YEAR
import java.util.Calendar.YEAR
import java.util.Locale
import java.util.concurrent.TimeUnit

fun getDurationToNextMinute(): Long {
    Calendar.getInstance().apply {
        val now = timeInMillis
        set(SECOND, 0)
        set(MILLISECOND, 0)
        add(MINUTE, 1)

        return timeInMillis - now
    }
}

fun getCurrentHour() = Calendar.getInstance().get(HOUR_OF_DAY)

fun getCurrentMinute() = Calendar.getInstance().get(MINUTE)

fun isWeekends(): Boolean {
    Calendar.getInstance().get(DAY_OF_WEEK).apply {
        return this == SATURDAY || this == SUNDAY
    }
}

fun calcTimeToStop(stop: BusStop.Time): String? {
    Calendar.getInstance().apply {
        set(HOUR_OF_DAY, stop.hour)
        set(MINUTE, stop.minute)
        set(MILLISECOND, 0)
        set(SECOND, 0)

        val curr = Calendar.getInstance().apply {
            set(MILLISECOND, 0)
            set(SECOND, 0)
        }
        val diff = time.time - curr.time.time
        val minutes = TimeUnit.MILLISECONDS.toMinutes(diff)
        val hours = TimeUnit.MILLISECONDS.toHours(diff)

        return if (hours == 0L && minutes == 0L) null else if (hours > 0) {
            "$hours ч, ${minutes - hours * 60} мин"
        } else {
            "$minutes мин"
        }
    }
}

fun prettyLastUpdate(dateParts: List<String>, timeParts: List<String>): String {
    Calendar.getInstance().apply {
        set(YEAR, dateParts[0].toInt())
        set(MONTH, dateParts[1].toInt())
        set(DAY_OF_MONTH, dateParts[2].toInt())
        set(HOUR_OF_DAY, timeParts[0].toInt())
        set(MINUTE, timeParts[1].toInt())
        set(SECOND, timeParts[2].toInt())

        return SimpleDateFormat("hh:mm - dd MMMM yyyy", Locale.US).format(time)
    }
}

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

fun getPrevWeek(fromWeek: String, format: String = "yyyy-MM-dd"): String {
    val parts = fromWeek.split("-")
    Calendar.getInstance().apply {
        firstDayOfWeek = MONDAY
        set(DAY_OF_WEEK, MONDAY)
        set(HOUR_OF_DAY, 0)
        set(MINUTE, 0)
        set(SECOND, 0)
        set(MILLISECOND, 0)
        set(YEAR, parts[0].toInt())
        set(MONTH, parts[1].toInt() - 1)
        set(DAY_OF_MONTH, parts[2].toInt())
        add(WEEK_OF_YEAR, -1)

        return SimpleDateFormat(format, Locale.US).format(time)
    }
}

fun getNextWeek(fromWeek: String? = null, format: String = "yyyy-MM-dd"): String {
    Calendar.getInstance().apply {
        firstDayOfWeek = MONDAY
        set(DAY_OF_WEEK, MONDAY)
        set(HOUR_OF_DAY, 0)
        set(MINUTE, 0)
        set(SECOND, 0)
        set(MILLISECOND, 0)
        if (fromWeek != null) {
            val parts = fromWeek.split("-")

            set(YEAR, parts[0].toInt())
            set(MONTH, parts[1].toInt() - 1)
            set(DAY_OF_MONTH, parts[2].toInt())
        }
        add(WEEK_OF_YEAR, 1)
        
        return SimpleDateFormat(format, Locale.US).format(time)
    }
}

fun formatWeek(apiWeek: String): String {
    val parts = apiWeek.split("-")
    return "${parts[2]}.${parts[1]}.${parts[0]}"
}