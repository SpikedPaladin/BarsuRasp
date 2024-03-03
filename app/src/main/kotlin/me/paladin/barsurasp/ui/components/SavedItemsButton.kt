package me.paladin.barsurasp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Button
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import me.paladin.barsurasp.R
import me.paladin.barsurasp.ui.components.sheets.ModalSheet
import me.paladin.barsurasp.ui.icons.Group
import me.paladin.barsurasp.ui.theme.BarsuRaspTheme

@Composable
fun SavedItemsButton(
    mainGroup: String?,
    savedItems: Set<String>,
    selectAction: (String) -> Unit,
    starAction: (String) -> Unit,
    openFaculties: () -> Unit
) {
    var visible by remember { mutableStateOf(false) }

    FloatingActionButton(onClick = {
        if (savedItems.isEmpty())
            openFaculties()
        else
            visible = true
    }) {
        Icon(
            imageVector = Icons.Outlined.Group,
            contentDescription = stringResource(R.string.timetable_change_group)
        )
    }

    SavedSheet(
        visible = visible,
        mainGroup = mainGroup,
        savedItems = savedItems,
        selectAction = selectAction,
        starAction = starAction,
        openFaculties = openFaculties
    ) {
        visible = false
    }
}

@Composable
private fun SavedSheet(
    visible: Boolean,
    mainGroup: String?,
    savedItems: Set<String>,
    selectAction: (String) -> Unit,
    starAction: (String) -> Unit,
    openFaculties: () -> Unit,
    onDismiss: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()

    ModalSheet(visible, onDismiss) { sheetState ->
        SavedSheetContent(
            mainGroup = mainGroup,
            savedItems = savedItems,
            selectAction = { item ->
                coroutineScope
                    .launch { sheetState.hide() }
                    .invokeOnCompletion {
                        onDismiss()
                        selectAction(item)
                    }
            },
            starAction = starAction,
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
    selectAction: (String) -> Unit,
    starAction: (String) -> Unit,
    openFaculties: () -> Unit
) {
    LazyColumn(
        Modifier.padding(bottom = 16.dp, start = 8.dp, end = 8.dp)
    ) {
        if (mainGroup != null && !savedItems.hasItem(mainGroup))
            item {
                val (title, subtitle) = mainGroup.splitItem()

                Text(
                    text = "Выбрано",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .padding(4.dp)
                )
                SavedItem(
                    title = title,
                    subtitle = subtitle,
                    selected = true,
                    isSaved = false,
                    onStar = { starAction(mainGroup) }
                )
                Spacer(Modifier.height(12.dp))
            }
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
            savedItems.indexOfFirst { it.splitItem().first == mainGroup.splitItem().first }
        else -1

        itemsIndexed(savedItems.toList()) { index, item ->
            val (title, subtitle) = item.splitItem()

            Spacer(Modifier.size(2.dp))
            SavedItem(
                title = title,
                subtitle = subtitle,
                selected = title == mainGroup?.splitItem()?.first,
                onStar = { starAction(item) },
                onClick = { selectAction(title) }
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
    subtitle: String? = null,
    selected: Boolean,
    isSaved: Boolean = true,
    onClick: (() -> Unit)? = null,
    onStar: () -> Unit
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
            .let {
                if (onClick != null)
                    return@let it.clickable(onClick = onClick)
                else return@let it
            }
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
        IconButton(onClick = onStar) {
            Icon(
                painter = if (isSaved) painterResource(R.drawable.ic_star)
                else painterResource(R.drawable.ic_star_outline),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}

private fun Set<String>.hasItem(item: String): Boolean {
    for (element in this) {
        if (element.splitItem().first == item.splitItem().first)
            return true
    }
    return false
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
            SavedItem(title = "ТОСП11", subtitle = "ИФ", selected = true, onStar = {}, onClick = {})
        }
    }
}