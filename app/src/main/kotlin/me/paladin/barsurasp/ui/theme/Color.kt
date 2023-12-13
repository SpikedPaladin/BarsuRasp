package me.paladin.barsurasp.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color

val Purple80 = Color(0xFFD0BCFF)
val PurpleGrey80 = Color(0xFFCCC2DC)
val Pink80 = Color(0xFFEFB8C8)

val Purple40 = Color(0xFF6650a4)
val PurpleGrey40 = Color(0xFF625b71)
val Pink40 = Color(0xFF7D5260)

var greenSchemeColor by mutableStateOf(Color(0xFF689F38))
var yellowSchemeColor by mutableStateOf(Color(0xFFFDD835))
var cyanSchemeColor by mutableStateOf(Color(0xFF00BCD4))
var purpleSchemeColor by mutableStateOf(Color(0xFF673AB7))

var ColorScheme.green: Color
    get() = greenSchemeColor
    set(value) { greenSchemeColor = value }
var ColorScheme.yellow: Color
    get() = yellowSchemeColor
    set(value) { yellowSchemeColor = value }
var ColorScheme.cyan: Color
    get() = cyanSchemeColor
    set(value) { cyanSchemeColor = value }
var ColorScheme.purple: Color
    get() = purpleSchemeColor
    set(value) { purpleSchemeColor = value }

fun ColorScheme.applyCustomColors(darkTheme: Boolean) {
    if (darkTheme) {
        green = Color(0xFFB2FF59)
        yellow = Color(0xFFFFF176)
        cyan = Color(0xFF4DD0E1)
        purple = Color(0xFF9575CD)
    } else {
        green = Color(0xFF689F38)
        yellow = Color(0xFFFDD835)
        cyan = Color(0xFF00BCD4)
        purple = Color(0xFF673AB7)
    }
}