package me.paladin.barsurasp.ui.components.barsu

import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import me.paladin.barsurasp.models.Sublesson

@Composable
fun SublessonItem(
    sublesson: Sublesson,
    openTeacher: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    Column(
        Modifier.combinedClickable(
            onClick = {},
            onLongClick = { expanded = true }
        )
    ) {
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            DropdownMenuItem(
                leadingIcon = {
                    Icon(imageVector = Icons.Outlined.Person, contentDescription = null)
                },
                text = { Text(text = "Расписание преподавателя") },
                onClick = {
                    expanded = false
                    openTeacher(sublesson.teacher)
                }
            )
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = sublesson.name,
                style = MaterialTheme.typography.titleMedium
            )
            sublesson.subgroup?.let {
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = it, style = MaterialTheme.typography.bodyMedium)
            }
            Spacer(modifier = Modifier.weight(1F))
            LessonType(sublesson.type)
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                modifier = Modifier.size(18.dp),
                imageVector = Icons.Outlined.Person,
                contentDescription = null
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = sublesson.teacher,
                style = MaterialTheme.typography.bodyMedium
            )
            sublesson.place?.let {
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = it, style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}