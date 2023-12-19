package me.paladin.barsurasp.glance.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.background
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.width
import androidx.glance.text.FontStyle
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import me.paladin.barsurasp.models.Lesson
import me.paladin.barsurasp.models.Sublesson

@Composable
fun GlanceLessonItem(lesson: Lesson, showDivider: Boolean) {
    Column(
        GlanceModifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp)
            .padding(bottom = 2.dp)
    ) {
        when (lesson) {
            is Lesson.Group -> if (lesson.sublessons != null) {
                Text(
                    text = lesson.time,
                    style = TextStyle(
                        color = GlanceTheme.colors.onBackground,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                )
                for (sublesson in lesson.sublessons) {
                    GlanceSublessonItem(sublesson = sublesson)
                }
            }

            is Lesson.Teacher -> {
                Text(
                    text = lesson.time,
                    style = TextStyle(
                        color = GlanceTheme.colors.onBackground,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                )
                Row {
                    Text(
                        text = lesson.name!!,
                        style = TextStyle(
                            color = GlanceTheme.colors.onBackground,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Spacer(GlanceModifier.width(6.dp))
                    Text(
                        text = lesson.type!!,
                        style = TextStyle(color = GlanceTheme.colors.onBackground)
                    )
                }
                Row {
                    Text(
                        text = lesson.groups!!,
                        style = TextStyle(color = GlanceTheme.colors.onBackground)
                    )
                    lesson.place?.let {
                        Spacer(GlanceModifier.width(6.dp))
                        Text(text = it, style = TextStyle(color = GlanceTheme.colors.onBackground))
                    }
                }
            }
        }

        if (showDivider) Spacer(
            GlanceModifier
                .fillMaxWidth()
                .height(1.dp)
                .background(GlanceTheme.colors.outline)
        )
    }
}

@Composable
private fun GlanceSublessonItem(sublesson: Sublesson) {
    Row {
        Text(
            text = sublesson.name,
            style = TextStyle(
                color = GlanceTheme.colors.onBackground,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        )
        sublesson.subgroup?.let {
            Spacer(GlanceModifier.width(6.dp))
            Text(
                text = it,
                style = TextStyle(
                    color = GlanceTheme.colors.onBackground,
                    fontStyle = FontStyle.Italic
                )
            )
        }
        Spacer(GlanceModifier.width(6.dp))
        Text(text = sublesson.type, style = TextStyle(color = GlanceTheme.colors.onBackground))
    }
    Row {
        Text(text = sublesson.teacher, style = TextStyle(color = GlanceTheme.colors.onBackground))
        sublesson.place?.let {
            Spacer(GlanceModifier.width(6.dp))
            Text(text = it, style = TextStyle(color = GlanceTheme.colors.onBackground))
        }
    }
}