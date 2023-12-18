package me.paladin.barsurasp.ui.screens

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelStoreOwner
import me.paladin.barsurasp.R
import me.paladin.barsurasp.models.Timetable
import me.paladin.barsurasp.ui.components.barsu.TimetableList
import me.paladin.barsurasp.ui.components.barsu.WeekSelector
import me.paladin.barsurasp.ui.components.bus.BusesCard
import me.paladin.barsurasp.ui.components.sheets.SavedSheet
import me.paladin.barsurasp.ui.icons.Group
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
                title = if (mainGroup != "") mainGroup else null,
                lastUpdate = if (uiState is UiState.Success) {
                    when (val timetable = (uiState as UiState.Success).data) {
                        is Timetable.Group -> timetable.lastUpdate
                        is Timetable.Teacher -> timetable.lastUpdate
                    }
                } else null,
                refreshAction = { viewModel.refreshTimetable() },
                settingsAction = navigateToSettings
            )
        },
        floatingActionButton = {
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
                groupSelected = { viewModel.setMainGroup(it) },
                groupRemoved = { viewModel.saveItem(it) },
                openFaculties = openFaculties
            ) {
                visible = false
            }
        }
    ) { paddingValues ->
        Crossfade(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(paddingValues)
                .fillMaxWidth(),
            targetState = uiState,
            label = "mainState"
        ) { state ->
            when (state) {
                UiState.Loading -> LoadingState()

                is UiState.Success -> SuccessState(
                    viewModel = viewModel,
                    timetable = state.data,
                    showBuses = showBuses,
                    busesViewModel = busesViewModel,
                    viewModelStoreOwner = viewModelStoreOwner,
                    selectedWeek = selectedWeek,
                    openBusConfig = openBusConfig
                )

                is UiState.Error -> ErrorState(
                    state = state,
                    viewModel = viewModel,
                    showBuses = showBuses,
                    busesViewModel = busesViewModel,
                    viewModelStoreOwner = viewModelStoreOwner,
                    week = selectedWeek,
                    openFaculties = openFaculties,
                    openBusConfig = openBusConfig,
                    updateTimetable = { viewModel.updateTimetable() },
                )
            }
        }
    }
}

@Composable
private fun SuccessState(
    viewModel: MainViewModel,
    timetable: Timetable,
    showBuses: Boolean,
    busesViewModel: BusesViewModel,
    viewModelStoreOwner: ViewModelStoreOwner,
    selectedWeek: String,
    openBusConfig: () -> Unit
) {
    BaseState(
        viewModel = viewModel,
        showBuses = showBuses,
        selectedWeek = selectedWeek,
        busesViewModel = busesViewModel,
        viewModelStoreOwner = viewModelStoreOwner,
        openBusConfig = openBusConfig
    ) {
        TimetableList(timetable)
    }
}

@Composable
private fun LoadingState() {
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

@Composable
private fun ErrorState(
    state: UiState.Error,
    viewModel: MainViewModel,
    showBuses: Boolean,
    week: String,
    busesViewModel: BusesViewModel,
    viewModelStoreOwner: ViewModelStoreOwner,
    openBusConfig: () -> Unit,
    openFaculties: () -> Unit,
    updateTimetable: () -> Unit,
) {
    BaseState(
        viewModel = viewModel,
        showBuses = showBuses,
        showWeek = !state.noGroup,
        selectedWeek = week,
        busesViewModel = busesViewModel,
        viewModelStoreOwner = viewModelStoreOwner,
        openBusConfig = openBusConfig
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
private fun BaseState(
    viewModel: MainViewModel,
    showWeek: Boolean = true,
    showBuses: Boolean,
    selectedWeek: String,
    busesViewModel: BusesViewModel,
    viewModelStoreOwner: ViewModelStoreOwner,
    openBusConfig: () -> Unit,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (showWeek)
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

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            content = content
        )

        if (showBuses)
            BusesCard(busesViewModel, viewModelStoreOwner, openBusConfig)
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
            Text(text = title ?: stringResource(R.string.timetable_title))
        },
        actions = {
            val tooltipState = rememberTooltipState()

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