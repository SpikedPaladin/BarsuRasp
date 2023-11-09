package me.paladin.barsurasp.ui.screens

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import me.paladin.barsurasp.R
import me.paladin.barsurasp.ui.components.BusesCard
import me.paladin.barsurasp.ui.components.TimetableList
import me.paladin.barsurasp.ui.viewmodels.MainViewModel
import me.paladin.barsurasp.ui.viewmodels.UiState

@Composable
fun MainScreen(
    navigateToSettings: () -> Unit,
    openFaculties: () -> Unit
) {
    val viewModel: MainViewModel = viewModel()
    val uiState by viewModel.timetableFlow.collectAsState()
    val week by viewModel.week.collectAsState()

    Scaffold(
        topBar = { MainToolbar({ viewModel.refreshTimetable() }, navigateToSettings) },
        floatingActionButton = {
            FloatingActionButton(onClick = openFaculties) {
                Icon(
                    painter = painterResource(R.drawable.ic_group),
                    contentDescription = stringResource(R.string.timetable_change_group)
                )
            }
        }
    ) { paddingValues ->
        Crossfade(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxWidth(),
            targetState = uiState,
            label = "mainState"
        ) { state ->
            when (state) {
                UiState.Loading -> {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator()
                        Spacer(modifier = Modifier.size(8.dp))
                        Text(text = stringResource(R.string.timetable_loading))
                    }
                }

                is UiState.Success -> {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                    ) {
                        TimetableList(state.data)

                        Spacer(modifier = Modifier.weight(1F))

                        Column {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.height(48.dp)
                            ) {
                                Text(
                                    style = MaterialTheme.typography.titleMedium,
                                    text = stringResource(R.string.timetable_next_week)
                                )
                                Switch(
                                    checked = week == "next",
                                    onCheckedChange = { viewModel.changeWeek(if (week == "current") "next" else "current") })
                            }
                            Text(text = "Группа: ${state.data.group}", style = MaterialTheme.typography.labelMedium)
                            Text(text = state.data.lastUpdate, style = MaterialTheme.typography.labelSmall)
                        }
                    }
                }

                is UiState.Error -> {
                    if (state.noTimetable) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Spacer(modifier = Modifier.height(24.dp))
                            Text(
                                style = MaterialTheme.typography.titleLarge,
                                text = stringResource(R.string.timetable_error_empty)
                            )
                            Spacer(modifier = Modifier.weight(1F))

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    style = MaterialTheme.typography.titleMedium,
                                    text = stringResource(R.string.timetable_next_week)
                                )
                                Switch(
                                    checked = week == "next",
                                    onCheckedChange = { viewModel.changeWeek(if (week == "current") "next" else "current") })
                            }
                        }
                    } else if (state.noGroup) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(text = stringResource(R.string.timetable_error_no_group))
                            Spacer(modifier = Modifier.height(12.dp))
                            Button(onClick = openFaculties) {
                                Text(text = stringResource(R.string.timetable_choose_group))
                            }
                        }
                    } else if (state.noInternet) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(text = stringResource(R.string.general_error_network))
                            Spacer(modifier = Modifier.height(12.dp))
                            Button(onClick = { viewModel.updateTimetable() }) {
                                Text(text = stringResource(R.string.general_refresh))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MainToolbar(
    refreshAction: () -> Unit,
    settingsAction: () -> Unit
) {
    TopAppBar(
        title = {
            Text(text = stringResource(R.string.timetable_title))
        },
        actions = {
            IconButton(onClick = refreshAction) {
                Icon(
                    painter = painterResource(R.drawable.ic_refresh),
                    contentDescription = null
                )
            }
            IconButton(onClick = settingsAction) {
                Icon(
                    imageVector = Icons.Outlined.Settings,
                    contentDescription = stringResource(R.string.description_settings)
                )
            }
        }
    )
}
