package me.paladin.barsurasp.models

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable

enum class AppTheme {
    DAY,
    NIGHT,
    AUTO;

    @Composable
    fun isDark() = when (this) {
        DAY -> false
        NIGHT -> true
        else -> isSystemInDarkTheme()
    }

    fun toPref(): String {
        return when (this) {
            DAY -> "day"
            NIGHT -> "night"
            AUTO -> "auto"
        }
    }

    fun toIndex(): Int {
        return when (this) {
            DAY -> 0
            NIGHT -> 1
            AUTO -> 2
        }
    }

    override fun toString(): String {
        return when (this) {
            DAY -> "Дневная"
            NIGHT -> "Ночная"
            AUTO -> "Автоматически"
        }
    }

    companion object {

        fun fromIndex(index: Int): AppTheme {
            return when (index) {
                0 -> DAY
                1 -> NIGHT
                else -> AUTO
            }
        }

        fun fromPref(pref: String?): AppTheme {
            return when (pref) {
                "day" -> DAY
                "night" -> NIGHT
                else -> AUTO
            }
        }
    }
}