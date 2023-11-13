package me.paladin.barsurasp.ui.screens

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import me.paladin.barsurasp.R
import me.paladin.barsurasp.ui.components.ExpandableCard
import me.paladin.barsurasp.ui.components.GroupItem
import me.paladin.barsurasp.ui.viewmodels.FacultiesUiState
import me.paladin.barsurasp.ui.viewmodels.FacultiesViewModel
import me.paladin.barsurasp.ui.viewmodels.MainViewModel

@Composable
fun FacultiesScreen(
    mainViewModel: MainViewModel = viewModel(),
    updateMainGroup: Boolean = false,
    groupSelected: (group: String?) -> Unit
) {
    val viewModel: FacultiesViewModel = viewModel()
    val uiState by viewModel.groupFlow.collectAsState()

    Scaffold(
        topBar = {
            FacultiesToolbar(refreshAction = { viewModel.refresh() }) {
                if (updateMainGroup)
                    groupSelected(null)
            }
        }
    ) { paddingValues ->
        Crossfade(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            targetState = uiState,
            label = "facultiesState"
        ) { state ->
            when (state) {
                FacultiesUiState.Loading -> {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator()
                        Text(text = stringResource(R.string.faculty_loading))
                    }
                }

                is FacultiesUiState.Success -> {
                    LazyColumn(Modifier.fillMaxSize()) {
                        items(state.data) {
                            ExpandableCard(title = it.name, outlined = true) {
                                for (speciality in it.specialities) {
                                    ExpandableCard(title = speciality.name) {
                                        for (group in speciality.groups) {
                                            GroupItem(group = group) { selectedGroup ->
                                                groupSelected(selectedGroup)

                                                if (updateMainGroup)
                                                    mainViewModel.setMainGroup(selectedGroup)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                is FacultiesUiState.Error -> {

                }
            }
        }
    }
}

@Composable
fun FacultiesToolbar(
    refreshAction: () -> Unit,
    backAction: (() -> Unit)? = null
) {
    TopAppBar(
        title = {
            Text(text = stringResource(R.string.faculty_title))
        },
        navigationIcon = {
            if (backAction != null) {
                IconButton(onClick = backAction) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Default.ArrowBack,
                        contentDescription = stringResource(R.string.description_back)
                    )
                }
            }
        },
        actions = {
            IconButton(onClick = refreshAction) {
                Icon(imageVector = Icons.Filled.Refresh, contentDescription = null)
            }
        }
    )
}