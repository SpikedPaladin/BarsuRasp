package me.paladin.barsurasp.ui.screens

import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RichTooltip
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelStoreOwner
import me.paladin.barsurasp.R
import me.paladin.barsurasp.ui.components.SavedItemsButton
import me.paladin.barsurasp.ui.components.barsu.TimetableList
import me.paladin.barsurasp.ui.components.barsu.WeekSelector
import me.paladin.barsurasp.ui.components.bus.BusesCard
import me.paladin.barsurasp.ui.viewmodels.BusesViewModel
import me.paladin.barsurasp.ui.viewmodels.MainViewModel
import me.paladin.barsurasp.ui.viewmodels.UiState
import me.paladin.barsurasp.utils.getCurrentWeek
import me.paladin.barsurasp.utils.getNextWeek
import me.paladin.barsurasp.utils.getPrevWeek

@Composable
fun MainScreen(
    viewModel: MainViewModel,
    busesViewModel: BusesViewModel,
    viewModelStoreOwner: ViewModelStoreOwner,
    navigateToSettings: () -> Unit,
    openFaculties: () -> Unit,
    openBusConfig: () -> Unit
) {
    val selectedWeek by viewModel.week.collectAsState()
    val showBuses by viewModel.showBuses.collectAsState()
    val mainGroup by viewModel.mainGroup.collectAsState()
    val savedItems by viewModel.savedItems.collectAsState()
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            MainToolbar(
                title = if (!mainGroup.isNullOrEmpty()) mainGroup else null,
                lastUpdate = (uiState as? UiState.Success)?.data?.lastSiteUpdate,
                refreshAction = { viewModel.refreshTimetable() },
                settingsAction = navigateToSettings
            )
        },
        floatingActionButton = {
            SavedItemsButton(
                mainGroup = mainGroup,
                savedItems = savedItems,
                groupSelected = { viewModel.setMainGroup(it) },
                groupRemoved = { viewModel.saveItem(it) },
                openFaculties = openFaculties
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            item {
                WeekSelector(
                    modifier = Modifier.padding(
                        top = 8.dp,
                        bottom = 8.dp
                    ),
                    week = selectedWeek,
                    prevClicked = { viewModel.setWeek(getPrevWeek(selectedWeek)) },
                    nextClicked = { viewModel.setWeek(getNextWeek(selectedWeek)) },
                    weekClicked = { viewModel.setWeek(getCurrentWeek()) }
                )
            }

            item {
                Crossfade(
                    modifier = Modifier
                        .fillMaxWidth()
                        .animateContentSize(),
                    targetState = uiState,
                    label = "mainState"
                ) { state ->
                    when (state) {
                        UiState.Loading -> LoadingState()

                        is UiState.Success -> TimetableList(
                            state.data,
                            openTeacher = { viewModel.setMainGroup(it) }
                        )

                        is UiState.Error -> ErrorState(
                            state = state,
                            openFaculties = openFaculties,
                            updateTimetable = { viewModel.updateTimetable() },
                        )
                    }
                }
            }

            if (showBuses) item {
                BusesCard(busesViewModel, viewModelStoreOwner, openBusConfig)
            }
        }
    }
}

@Composable
private fun LoadingState() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 150.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircularProgressIndicator()
        Spacer(modifier = Modifier.size(8.dp))
        Text(text = stringResource(R.string.timetable_loading))
    }
}

@Composable
private fun ErrorState(
    state: UiState.Error,
    openFaculties: () -> Unit,
    updateTimetable: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 150.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        if (state.noTimetable) {
            Text(
                style = MaterialTheme.typography.titleLarge,
                text = stringResource(R.string.timetable_error_empty)
            )
        } else if (state.noGroup) {
            Text(text = stringResource(R.string.timetable_error_no_group))
            Spacer(modifier = Modifier.height(12.dp))
            Button(onClick = openFaculties) {
                Text(text = stringResource(R.string.timetable_choose_group))
            }
        } else if (state.noInternet) {
            Text(text = stringResource(R.string.general_error_network))
            Spacer(modifier = Modifier.height(12.dp))
            Button(onClick = updateTimetable) {
                Text(text = stringResource(R.string.general_refresh))
            }
        }
    }
}

@Composable
private fun MainToolbar(
    title: String? = null,
    lastUpdate: String? = null,
    refreshAction: () -> Unit,
    settingsAction: () -> Unit
) {
    TopAppBar(
        title = {
            Text(text = title?.split(":", limit = 2)?.get(0) ?: stringResource(R.string.timetable_title))
        },
        actions = {
            val tooltipState = rememberTooltipState(isPersistent = true)

            TooltipBox(
                positionProvider = TooltipDefaults.rememberRichTooltipPositionProvider(),
                tooltip = {
                    RichTooltip(
                        title = { Text("Последнее обновление на сайте") }
                    ) {
                        Text(text = lastUpdate ?: "Нет информации")
                    }
                },
                state = tooltipState
            ) {
                IconButton(onClick = refreshAction) {
                    Icon(
                        imageVector = Icons.Outlined.Refresh,
                        contentDescription = null
                    )
                }
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