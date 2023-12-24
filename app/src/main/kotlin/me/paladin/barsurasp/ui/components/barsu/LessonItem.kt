package me.paladin.barsurasp.ui.components.barsu

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import me.paladin.barsurasp.R
import me.paladin.barsurasp.models.Lesson
import me.paladin.barsurasp.ui.icons.Group

@Composable
fun LessonItem(lesson: Lesson) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                modifier = Modifier.size(16.dp),
                painter = painterResource(id = R.drawable.ic_time),
                contentDescription = null
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = lesson.fullTime,
                style = MaterialTheme.typography.titleSmall
            )
        }

        when (lesson) {
            is Lesson.Group -> {
                Column {
                    if (lesson.sublessons != null) for (sublesson in lesson.sublessons) {
                        SublessonItem(sublesson = sublesson)
                    }
                }
            }
            is Lesson.Teacher -> {
                if (lesson.isEmpty)
                    return@Column

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = lesson.name!!,
                            style = MaterialTheme.typography.titleMedium
                        )
                        Spacer(modifier = Modifier.weight(1F))
                        LessonType(lesson.type!!)
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            modifier = Modifier.size(18.dp),
                            imageVector = Icons.Outlined.Group,
                            contentDescription = null
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = lesson.groups!!,
                            style = MaterialTheme.typography.bodyMedium
                        )
                        lesson.place?.let {
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = it, style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            }
        }
    }
}