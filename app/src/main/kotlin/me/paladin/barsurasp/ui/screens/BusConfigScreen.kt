package me.paladin.barsurasp.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import me.paladin.barsurasp.R
import me.paladin.barsurasp.models.BusPath
import me.paladin.barsurasp.ui.icons.Bus
import me.paladin.barsurasp.ui.viewmodels.BusesViewModel

@Composable
fun BusConfigScreen(
    viewModel: BusesViewModel,
    openPathCreate: () -> Unit,
    backAction: () -> Unit,
    openBusList: () -> Unit
) {
    val buses = viewModel.paths.collectAsState()

    Scaffold(
        topBar = { BusConfigToolbar(backAction) },
        floatingActionButtonPosition = FabPosition.Center,
        floatingActionButton = {
            OutlinedButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 12.dp, end = 12.dp),
                onClick = openBusList
            ) {
                Text(text = "Все автобусы")
            }
        }
    ) { paddingValues ->
        ScreenContent(
            buses = buses,
            addPath = { viewModel.addPath(it) },
            deletePath = { viewModel.deletePath(it) },
            openPathCreate = openPathCreate,
            modifier = Modifier.padding(paddingValues)
        )
    }
}

@Composable
fun ScreenContent(
    modifier: Modifier = Modifier,
    buses: State<List<BusPath>>,
    addPath: (BusPath) -> Unit,
    deletePath: (Int) -> Unit,
    openPathCreate: () -> Unit
) {
    LazyColumn(
        contentPadding = PaddingValues(start = 8.dp, end = 8.dp, bottom = 72.dp),
        modifier = modifier
            .fillMaxWidth()
    ) {
        item {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Outlined.Bus,
                    contentDescription = null,
                    modifier = Modifier.size(96.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Создай машруты чтобы отображать их на главном экране",
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center
                )
            }

            Text(
                text = "Рекомендуемые машруты",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary
            )
            RecommendedPath(title = "На Университет", subtitle = "Автобусы 14, 29, 31") {
                addPath(
                    BusPath(
                        title = "На Университет", buses = listOf(
                            Triple(14, "Стадион", true),
                            Triple(29, "Стадион", false),
                            Triple(31, "Сквер Героя Карвата", true)
                        )
                    )
                )
            }
            RecommendedPath(title = "На Уборевича", subtitle = "Автобусы 14, 29, 31") {
                addPath(
                    BusPath(
                        title = "На Уборевича", buses = listOf(
                            Triple(14, "Университет", false),
                            Triple(29, "Университет", true),
                            Triple(31, "Университет", false)
                        )
                    )
                )
            }
            Spacer(Modifier.size(12.dp))

            Text(
                text = "Машруты",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary
            )
        }

        itemsIndexed(buses.value) { index, item ->
            SavedPath(path = item) {
                deletePath(index)
            }
        }

        item {
            ElevatedButton(onClick = openPathCreate) {
                Text(text = "Добавить маршрут")
            }
        }
    }
}

@Composable
private fun SavedPath(path: BusPath, deleteClicked: () -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(text = path.title, modifier = Modifier.weight(1F))
        IconButton(onClick = deleteClicked) {
            Icon(imageVector = Icons.Outlined.Delete, contentDescription = null)
        }
    }
}

@Composable
private fun RecommendedPath(
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Row {
        Column(Modifier.weight(1F)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium
            )
            Text(text = subtitle, style = MaterialTheme.typography.bodySmall)
        }
        Button(onClick = onClick) {
            Text(text = "+")
        }
    }
}

@Composable
private fun BusConfigToolbar(backAction: () -> Unit) {
    TopAppBar(
        title = {
            Text(text = "Маршруты")
        },
        navigationIcon = {
            IconButton(onClick = backAction) {
                Icon(
                    imageVector = Icons.AutoMirrored.Default.ArrowBack,
                    contentDescription = stringResource(R.string.description_back)
                )
            }
        }
    )
}