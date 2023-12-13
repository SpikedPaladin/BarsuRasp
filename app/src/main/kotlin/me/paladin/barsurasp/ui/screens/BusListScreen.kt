package me.paladin.barsurasp.ui.screens

import android.widget.Toast
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import me.paladin.barsurasp.models.BusDirection
import me.paladin.barsurasp.ui.components.RoundedBox
import me.paladin.barsurasp.ui.viewmodels.BusState
import me.paladin.barsurasp.ui.viewmodels.BusViewModel
import me.paladin.barsurasp.ui.viewmodels.BusesUiState
import me.paladin.barsurasp.ui.viewmodels.BusesViewModel

@Composable
fun BusListScreen(
    viewModel: BusesViewModel,
    viewModelStoreOwner: ViewModelStoreOwner,
    busClicked: (Int) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    Scaffold(
        topBar = { BusListToolbar() }
    ) { paddingValues ->
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(paddingValues)
        ) {
            Crossfade(targetState = uiState, label = "busList") { state ->
                when (state) {
                    is BusesUiState.Success -> {
                        LazyColumn {
                            items(state.data) {
                                BusItem(it, viewModelStoreOwner, busClicked)
                            }
                        }
                    }
                    BusesUiState.Loading -> {
                        CircularProgressIndicator()
                    }
                    is BusesUiState.Error -> {
                        Column {
                            Text("Отсутсвует подключение к сети.")
                            Button(onClick = { viewModel.loadBuses() }) {
                                Text("Обновить")
                            }
                        }
                    }
                }
            }

        }
    }
}

@Composable
private fun BusItem(
    direction: BusDirection,
    viewModelStoreOwner: ViewModelStoreOwner,
    busClicked: (Int) -> Unit
) {
    val viewModel: BusViewModel = viewModel(
        viewModelStoreOwner = viewModelStoreOwner,
        factory = BusViewModel.Factory(direction.busNumber),
        key = direction.busNumber.toString()
    )
    val busState by viewModel.busState.collectAsState()
    val context = LocalContext.current

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .height(60.dp)
            .padding(start = 6.dp)
            .clickable { busClicked(direction.busNumber) }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1F)
        ) {
            RoundedBox(Modifier.size(48.dp)) {
                Text(
                    text = direction.busNumber.toString(),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Spacer(Modifier.size(6.dp))
            Text(text = direction.name)
        }

        Crossfade(
            modifier = Modifier.size(60.dp),
            targetState = busState,
            label = "busItem"
        ) { state ->
            when (state) {
                is BusState.Loaded -> {
                    var expanded by remember { mutableStateOf(false) }
                    DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                        DropdownMenuItem(
                            leadingIcon = {
                                Icon(imageVector = Icons.Outlined.Delete, contentDescription = null)
                            },
                            text = { Text(text = "Удалить") },
                            onClick = {
                                expanded = false
                                viewModel.delete()
                            }
                        )
                        Text(
                            text = "Последнее обновление:\n${state.info.lastFetch}",
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(4.dp)
                        )
                    }

                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        IconButton(onClick = { expanded = true }) {
                            Icon(imageVector = Icons.Filled.MoreVert, contentDescription = null)
                        }
                    }
                }
                is BusState.Loading -> {
                    val progress by viewModel.progress.collectAsState()

                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(progress = { progress })
                    }
                }
                BusState.None -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        IconButton(
                            onClick = {
                                viewModel.load {
                                    Toast.makeText(context, "Ошибка подключения к сети!", Toast.LENGTH_SHORT).show()
                                }
                            }
                        ) {
                            Icon(imageVector = Icons.Filled.Add, contentDescription = null)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun BusListToolbar() {
    TopAppBar(
        title = {
            Text(text = "Автобусы")
        }
    )
}