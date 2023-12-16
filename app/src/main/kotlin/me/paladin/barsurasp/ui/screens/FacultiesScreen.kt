package me.paladin.barsurasp.ui.screens

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import me.paladin.barsurasp.R
import me.paladin.barsurasp.ui.components.ExpandableCard
import me.paladin.barsurasp.ui.components.ExpandableItem
import me.paladin.barsurasp.ui.components.barsu.GroupItem
import me.paladin.barsurasp.ui.viewmodels.FacultiesUiState
import me.paladin.barsurasp.ui.viewmodels.FacultiesViewModel

@Composable
fun FacultiesScreen(
    backAction: (() -> Unit)? = null,
    groupSaved: ((group: String) -> Unit)? = null,
    groupSelected: (group: String, item: String) -> Unit
) {
    val viewModel: FacultiesViewModel = viewModel()
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            FacultiesToolbar(
                refreshAction = { viewModel.refresh() },
                backAction = backAction
            )
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
                    var expandedFaculty by remember { mutableStateOf(-1) }

                    LazyColumn(Modifier.fillMaxSize()) {
                        itemsIndexed(state.data) { index, item ->
                            ExpandableItem(
                                expanded = expandedFaculty == index,
                                onClick = {
                                    expandedFaculty = if (expandedFaculty == index)
                                        -1
                                    else index
                                },
                                title = {
                                    Text(
                                        text = item.name,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(16.dp),
                                        textAlign = TextAlign.Center,
                                        style = MaterialTheme.typography.titleMedium
                                    )
                                }
                            ) {
                                for (speciality in item.specialities) {
                                    ExpandableCard(title = speciality.name) {
                                        for (group in speciality.groups) {
                                            GroupItem(
                                                group = group,
                                                onSaveClick = if (groupSaved != null) {
                                                    { groupSaved("$group:${item.name}") }
                                                } else null,
                                                onClick = { groupSelected(group, "$group:${item.name}") }
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                is FacultiesUiState.Error -> {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .padding(24.dp)
                            .fillMaxSize()
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Warning,
                            contentDescription = null,
                            modifier = Modifier.size(96.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "Проблемы с подключением к сети. Проверьте подключение и попробуйте заново.",
                            textAlign = TextAlign.Center
                        )
                        Button(onClick = { viewModel.refresh() }) {
                            Text("Обновить")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun FacultiesToolbar(
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