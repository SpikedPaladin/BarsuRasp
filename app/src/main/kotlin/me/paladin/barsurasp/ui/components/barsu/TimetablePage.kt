package me.paladin.barsurasp.ui.components.barsu

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import me.paladin.barsurasp.models.DaySchedule
import me.paladin.barsurasp.models.Lesson
import me.paladin.barsurasp.models.Sublesson
import me.paladin.barsurasp.ui.theme.BarsuRaspTheme

@Composable
fun TimetablePage(day: DaySchedule) {
    ElevatedCard(
        elevation = CardDefaults.cardElevation(4.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column {
            PageTitle(day)

            HorizontalDivider()

            day.actualLessons?.forEachIndexed { index, it ->
                if (!it.isEmpty) {
                    LessonItem(lesson = it)

                    if (index < day.actualLessons!!.size - 1)
                        HorizontalDivider()
                }
            }
        }
    }
}

@Preview
@Composable
private fun TimetablePagePreview() {
    BarsuRaspTheme {
        TimetablePage(
            day = DaySchedule(
                date = "01.10",
                dayOfWeek = "ПН",
                week = "01.10.2023",
                lessons = listOf(
                    Lesson(
                        time = "8.00-9.00",
                        sublessons = listOf(
                            Sublesson(
                                type = "ЛК",
                                teacher = "Хуй",
                                name = "Хуйня"
                            )
                        )
                    ),
                    Lesson(
                        time = "9.35-11.00",
                        sublessons = listOf(
                            Sublesson(
                                type = "ПЗ",
                                teacher = "Чучело",
                                name = "Уебанство",
                                place = "4/307 Парк"
                            )
                        )
                    )
                )
            )
        )
    }
}