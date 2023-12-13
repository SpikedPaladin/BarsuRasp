package me.paladin.barsurasp.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.unit.dp
import me.paladin.barsurasp.ui.theme.cyan
import me.paladin.barsurasp.ui.theme.green
import me.paladin.barsurasp.ui.theme.purple
import me.paladin.barsurasp.ui.theme.yellow

@Composable
fun LessonType(
    type: String
) {
    val color = getTypeColor(type)

    Text(
        text = type,
        color = color,
        style = MaterialTheme.typography.labelMedium,
        modifier = Modifier
            .padding(end = 8.dp)
            .drawBehind {
                drawRoundRect(
                    color = color,
                    alpha = 0.2F,
                    topLeft = Offset(0F - 6.dp.toPx(), 0F - 2.dp.toPx()),
                    size = Size(this.size.width + 12.dp.toPx(), this.size.height + 4.dp.toPx()),
                    cornerRadius = CornerRadius((this.size.height + 4.dp.toPx()) / 2F)
                )
            }
    )
}

@Composable
fun getTypeColor(type: String) = when (type.uppercase()) {
    "ЛК" -> MaterialTheme.colorScheme.green
    "ПЗ" -> MaterialTheme.colorScheme.error
    "ЛЗ" -> MaterialTheme.colorScheme.yellow
    "СЗ" -> MaterialTheme.colorScheme.cyan
    "ЗАЧ" -> MaterialTheme.colorScheme.purple
    else -> MaterialTheme.colorScheme.onBackground
}