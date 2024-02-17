package me.paladin.barsurasp.ui.components.sheets

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import me.paladin.barsurasp.ui.theme.BarsuRaspTheme

@Composable
fun SavedSheet(
    visible: Boolean,
    mainGroup: String?,
    savedItems: Set<String>,
    groupSelected: (String) -> Unit,
    groupRemoved: (String) -> Unit,
    openFaculties: () -> Unit,
    onDismiss: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()

    ModalSheet(visible, onDismiss) { sheetState ->
        SavedSheetContent(
            mainGroup = mainGroup,
            savedItems = savedItems,
            groupSelected = { item ->
                coroutineScope
                    .launch { sheetState.hide() }
                    .invokeOnCompletion {
                        onDismiss()
                        groupSelected(item)
                    }
            },
            groupRemoved = groupRemoved,
            openFaculties = {
                coroutineScope
                    .launch { sheetState.hide() }
                    .invokeOnCompletion {
                        onDismiss()
                        openFaculties()
                    }
            }
        )
    }
}

@Composable
private fun SavedSheetContent(
    mainGroup: String?,
    savedItems: Set<String>,
    groupSelected: (String) -> Unit,
    groupRemoved: (String) -> Unit,
    openFaculties: () -> Unit,
) {
    LazyColumn(
        Modifier.padding(bottom = 16.dp, start = 8.dp, end = 8.dp)
    ) {
        item {
            Text(
                text = "Сохраненные",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .padding(4.dp)
            )
        }

        val selectedIndex = if (mainGroup != null)
            savedItems.indexOfFirst { it.startsWith(mainGroup) }
        else -1

        itemsIndexed(savedItems.toList()) { index, item ->
            val (title, subtitle) = item.splitItem()

            Spacer(Modifier.size(2.dp))
            SavedItem(
                title = title,
                subtitle = subtitle,
                selected = title == mainGroup,
                onRemove = { groupRemoved(item) },
                onClick = { groupSelected(title) }
            )
            Spacer(Modifier.size(2.dp))
            if (selectedIndex != index && selectedIndex - 1 != index && index != savedItems.size - 1)
                HorizontalDivider(Modifier.padding(start = 4.dp, end = 4.dp))
        }
        item {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
            ) {
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = openFaculties
                ) {
                    Text(text = "Все группы")
                }
            }
        }
    }
}

@Composable
private fun SavedItem(
    title: String,
    subtitle: String?,
    selected: Boolean,
    onRemove: () -> Unit,
    onClick: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.medium)
            .background(
                if (selected)
                    MaterialTheme.colorScheme.primary.copy(0.2F)
                else
                    Color.Transparent
            )
            .clickable(onClick = onClick)
    ) {
        Column(
            modifier = Modifier
                .padding(4.dp)
                .weight(1F)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium
            )
            subtitle?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        IconButton(onClick = onRemove) {
            Icon(
                imageVector = Icons.Filled.Star,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}

private fun String.splitItem(): Pair<String, String?> {
    val parts = split(":", limit = 2)
    val title = parts[0]
    val subtitle = if (parts.size > 1)
        if (parts[1] == "") null
        else parts[1]
    else null

    return Pair(title, subtitle)
}

@Preview
@Composable
private fun SavedItemPreview() {
    BarsuRaspTheme {
        Surface {
            SavedItem(title = "ТОСП11", subtitle = "ИФ", selected = true, onRemove = { /*TODO*/ }) {

            }
        }
    }
}