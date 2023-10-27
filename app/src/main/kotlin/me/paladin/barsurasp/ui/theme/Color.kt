package me.paladin.barsurasp.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val Purple80 = Color(0xFFD0BCFF)
val PurpleGrey80 = Color(0xFFCCC2DC)
val Pink80 = Color(0xFFEFB8C8)

val Purple40 = Color(0xFF6650a4)
val PurpleGrey40 = Color(0xFF625b71)
val Pink40 = Color(0xFF7D5260)

val ColorScheme.yellow: Color
    @Composable
    get() = if (isSystemInDarkTheme()) Color(0xFFFFF176) else Color(0xFFFDD835)

val ColorScheme.cyan: Color
    @Composable
    get() = if (isSystemInDarkTheme()) Color(0xFF4DD0E1) else Color(0xFF00BCD4)

val ColorScheme.purple: Color
    @Composable
    get() = if (isSystemInDarkTheme()) Color(0xFF9575CD) else Color(0xFF673AB7)