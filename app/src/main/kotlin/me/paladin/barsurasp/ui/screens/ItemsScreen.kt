package me.paladin.barsurasp.ui.screens

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.layout
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.lerp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.launch
import me.paladin.barsurasp.R
import me.paladin.barsurasp.ui.components.ExpandableCard
import me.paladin.barsurasp.ui.components.ExpandableItem
import me.paladin.barsurasp.ui.components.barsu.GroupItem
import me.paladin.barsurasp.ui.viewmodels.FacultiesUiState
import me.paladin.barsurasp.ui.viewmodels.FacultiesViewModel
import me.paladin.barsurasp.ui.viewmodels.TeachersUiState
import me.paladin.barsurasp.ui.viewmodels.TeachersViewModel

@Composable
fun FacultiesScreen(
    backAction: (() -> Unit)? = null,
    groupSaved: ((group: String) -> Unit)? = null,
    groupSelected: (group: String, item: String) -> Unit
) {
    val facultiesViewModel: FacultiesViewModel = viewModel()
    val teachersViewModel: TeachersViewModel = viewModel()

    val pagerState = rememberPagerState(pageCount = { 2 })

    Scaffold(
        topBar = {
            Column {
                ItemsToolbar(
                    refreshAction = {
                        if (pagerState.currentPage == 0)
                            facultiesViewModel.refresh()
                        else
                            teachersViewModel.refresh()
                    },
                    backAction = backAction
                )
                PrimaryTabRow(
                    selectedTabIndex = pagerState.currentPage,
                    indicator = {
                        Spacer(
                            Modifier
                                .pagerTabIndicatorOffset(pagerState)
                                .requiredHeight(3.dp)
                                .background(
                                    color = MaterialTheme.colorScheme.primary,
                                    shape = RoundedCornerShape(3.dp)
                                )
                        )
                    }
                ) {
                    val coroutineScope = rememberCoroutineScope()
                    val pages = listOf("Группы", "Преподаватели")

                    pages.forEachIndexed { index, page ->
                        Tab(
                            selected = true,
                            onClick = {
                                coroutineScope.launch { pagerState.animateScrollToPage(index) }
                            },
                            text = { Text(page) }
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        HorizontalPager(
            beyondBoundsPageCount = 1,
            state = pagerState,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (it) {
                0 -> GroupsPage(
                    facultiesViewModel,
                    groupSaved = groupSaved,
                    groupSelected = groupSelected
                )

                else -> TeachersPage(teachersViewModel)
            }
        }
    }
}

@Composable
private fun GroupsPage(
    viewModel: FacultiesViewModel,
    groupSaved: ((group: String) -> Unit)? = null,
    groupSelected: (group: String, item: String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    Crossfade(
        modifier = Modifier
            .fillMaxSize(),
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
                                            onClick = {
                                                groupSelected(
                                                    group,
                                                    "$group:${item.name}"
                                                )
                                            }
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

@Composable
private fun TeachersPage(viewModel: TeachersViewModel) {
    val uiState by viewModel.uiState.collectAsState()

    Crossfade(
        modifier = Modifier
            .fillMaxSize(),
        targetState = uiState,
        label = "facultiesState"
    ) { state ->
        when (state) {
            TeachersUiState.Loading -> {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator()
                    Text(text = stringResource(R.string.faculty_loading))
                }
            }

            is TeachersUiState.Success -> {
                var expandedDepartment by remember { mutableStateOf(-1) }

                LazyColumn(Modifier.fillMaxSize()) {
                    itemsIndexed(state.data) { index, item ->
                        ExpandableItem(
                            expanded = expandedDepartment == index,
                            onClick = {
                                expandedDepartment = if (expandedDepartment == index)
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
                            for (teacher in item.teachers) {
                                GroupItem(
                                    group = teacher,
                                    onSaveClick = {},
                                    onClick = {}
                                )
                            }
                        }
                    }
                }
            }

            is TeachersUiState.Error -> ErrorState(
                refreshAction = { viewModel.refresh() }
            )
        }
    }
}

@Composable
private fun ErrorState(refreshAction: () -> Unit) {
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
        Button(onClick = refreshAction) {
            Text("Обновить")
        }
    }
}

@Composable
private fun ItemsToolbar(
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

private fun Modifier.pagerTabIndicatorOffset(
    pagerState: PagerState
): Modifier = layout { measurable, constraints ->
    val fraction = pagerState.currentPageOffsetFraction
    val indicatorOffset = if (fraction > 0) {
        lerp(0.dp, constraints.maxWidth.toDp(), fraction).roundToPx()
    } else if (fraction < 0) {
        lerp(constraints.maxWidth.toDp(), 0.dp, -fraction).roundToPx()
    } else {
        if (pagerState.currentPage == 0)
            0
        else
            constraints.maxWidth
    }
    val placeable = measurable.measure(
        constraints.copy(
            minWidth = constraints.maxWidth / 2,
            maxWidth = constraints.maxWidth / 2
        )
    )
    layout(placeable.width, constraints.maxHeight) {
        placeable.placeRelative(
            indicatorOffset,
            constraints.maxHeight - placeable.height
        )
    }
}