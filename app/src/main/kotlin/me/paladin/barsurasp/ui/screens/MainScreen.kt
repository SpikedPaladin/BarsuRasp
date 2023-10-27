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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import me.paladin.barsurasp.Locator
import me.paladin.barsurasp.R
import me.paladin.barsurasp.ui.components.TimetableList
import me.paladin.barsurasp.ui.viewmodels.MainViewModel
import me.paladin.barsurasp.ui.viewmodels.UiState

@Composable
fun MainScreen(
    navigateToSettings: () -> Unit,
    openFaculties: () -> Unit
) {
    val viewModel: MainViewModel = viewModel(factory = Locator.mainViewModelFactory)
    val uiState by viewModel.timetableFlow.collectAsState()
    val week by viewModel.week.collectAsState()

    Scaffold(
        topBar = { MainToolbar(navigateToSettings) }
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
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        TimetableList(state.data)
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

                        Button(
                            modifier = Modifier.padding(top = 12.dp, bottom = 12.dp),
                            onClick = openFaculties
                        ) {
                            Text(text = stringResource(R.string.timetable_change_group))
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

                            Button(
                                modifier = Modifier.padding(top = 12.dp, bottom = 12.dp),
                                onClick = openFaculties
                            ) {
                                Text(text = stringResource(R.string.timetable_change_group))
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
fun MainToolbar(settingsAction: () -> Unit) {
    TopAppBar(
        title = {
            Text(text = stringResource(R.string.timetable_title))
        },
        actions = {
            IconButton(onClick = settingsAction) {
                Icon(
                    imageVector = Icons.Outlined.Settings,
                    contentDescription = stringResource(R.string.description_settings)
                )
            }
        }
    )
}
