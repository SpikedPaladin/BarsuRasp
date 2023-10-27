package me.paladin.barsurasp.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import me.paladin.barsurasp.models.Sublesson

@Composable
fun SublessonItem(sublesson: Sublesson) {
    Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = sublesson.name,
                style = MaterialTheme.typography.titleLarge
            )
            sublesson.subgroup?.let {
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = it)
            }
            Spacer(modifier = Modifier.weight(1F))
            LessonType(sublesson.type)
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(imageVector = Icons.Outlined.Person, contentDescription = "Aga")
            Text(text = sublesson.teacher)
            sublesson.place?.let {
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = it)
            }
        }
    }
}