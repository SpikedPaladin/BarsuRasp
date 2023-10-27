package me.paladin.barsurasp.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import me.paladin.barsurasp.R
import me.paladin.barsurasp.models.Lesson

@Composable
fun LessonItem(lesson: Lesson) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Row {
            Icon(
                painter = painterResource(id = R.drawable.ic_time),
                contentDescription = null
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = lesson.time,
                style = MaterialTheme.typography.titleMedium
            )
        }

        Column {
            when {
                lesson.sublessons != null -> for (sublesson in lesson.sublessons) {
                    SublessonItem(sublesson = sublesson)
                }
            }
        }
    }
}