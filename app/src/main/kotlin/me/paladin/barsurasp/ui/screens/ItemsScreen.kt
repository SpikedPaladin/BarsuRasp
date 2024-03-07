package me.paladin.barsurasp.ui.screens

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.DockedSearchBar
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshContainer
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.layout
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.lerp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.launch
import me.paladin.barsurasp.R
import me.paladin.barsurasp.models.Item
import me.paladin.barsurasp.ui.components.ExpandableCard
import me.paladin.barsurasp.ui.components.ExpandableItem
import me.paladin.barsurasp.ui.components.SavedItem
import me.paladin.barsurasp.ui.components.barsu.GroupItem
import me.paladin.barsurasp.ui.viewmodels.GroupsUiState
import me.paladin.barsurasp.ui.viewmodels.ItemsViewModel
import me.paladin.barsurasp.ui.viewmodels.TeachersUiState

@Composable
fun ItemsScreen(
    savedItems: List<Item> = listOf(),
    backAction: (() -> Unit)? = null,
    itemSaved: ((item: Item) -> Unit)? = null,
    itemSelected: (item: Item) -> Unit
) {
    val viewModel: ItemsViewModel = viewModel()
    val searchResults by viewModel.searchResults.collectAsState()

    val pagerState = rememberPagerState(pageCount = { 2 })

    Scaffold(
        topBar = {
            Column {
                ItemsToolbar(
                    searchQuery = viewModel.searchQuery,
                    onQueryChange = { viewModel.onSearchQueryChanged(it) },
                    searchResults = searchResults,
                    itemSelected = itemSelected,
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
                    viewModel,
                    savedItems = savedItems,
                    groupSaved = itemSaved,
                    groupSelected = itemSelected
                )

                else -> TeachersPage(
                    viewModel,
                    savedItems = savedItems,
                    teacherSaved = itemSaved,
                    teacherSelected = itemSelected
                )
            }
        }
    }
}

@Composable
private fun GroupsPage(
    viewModel: ItemsViewModel,
    savedItems: List<Item>,
    groupSaved: ((item: Item) -> Unit)? = null,
    groupSelected: (item: Item) -> Unit
) {
    val uiState by viewModel.groupsUiState.collectAsState()
    val refreshState = rememberPullToRefreshState()

    if (refreshState.isRefreshing) {
        LaunchedEffect(true) {
            viewModel.refreshGroups().invokeOnCompletion {
                refreshState.endRefresh()
            }
        }
    }

    Box(
        modifier = Modifier
            .nestedScroll(refreshState.nestedScrollConnection)
            .fillMaxSize()
    ) {
        Crossfade(
            modifier = Modifier
                .fillMaxWidth(),
            targetState = uiState,
            label = "facultiesState"
        ) { state ->
            if (state is GroupsUiState.Success) {
                var expandedFaculty by remember { mutableIntStateOf(-1) }

                LazyColumn(Modifier.fillMaxSize()) {
                    itemsIndexed(state.data) { index, faculty ->
                        ExpandableItem(
                            expanded = expandedFaculty == index,
                            onClick = {
                                expandedFaculty = if (expandedFaculty == index)
                                    -1
                                else index
                            },
                            title = {
                                Text(
                                    text = faculty.name,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    textAlign = TextAlign.Center,
                                    style = MaterialTheme.typography.titleMedium
                                )
                            }
                        ) {
                            for (speciality in faculty.specialities) {
                                ExpandableCard(title = speciality.name) {
                                    for (group in speciality.groups) {
                                        GroupItem(
                                            item = Item(group, faculty.name),
                                            saved = savedItems.find { it.title == group } != null,
                                            onSaveClick = groupSaved,
                                            onClick = groupSelected
                                        )
                                    }
                                }
                            }

                            if (state.data.size != index + 1)
                                HorizontalDivider(Modifier.padding(horizontal = 8.dp))
                        }
                    }
                }
            }

            if (state is GroupsUiState.Error) ErrorState(refreshAction = { viewModel.refreshGroups() })
        }
        PullToRefreshContainer(
            modifier = Modifier.align(Alignment.TopCenter),
            state = refreshState
        )
    }
}

@Composable
private fun TeachersPage(
    viewModel: ItemsViewModel,
    savedItems: List<Item>,
    teacherSaved: ((item: Item) -> Unit)? = null,
    teacherSelected: (item: Item) -> Unit
) {
    val uiState by viewModel.teachersUiState.collectAsState()
    val refreshState = rememberPullToRefreshState()

    if (refreshState.isRefreshing) {
        LaunchedEffect(true) {
            viewModel.refreshTeachers().invokeOnCompletion {
                refreshState.endRefresh()
            }
        }
    }

    Box(
        modifier = Modifier
            .nestedScroll(refreshState.nestedScrollConnection)
            .fillMaxSize()
    ) {
        Crossfade(
            modifier = Modifier
                .fillMaxSize(),
            targetState = uiState,
            label = "facultiesState"
        ) { state ->
                if (state is TeachersUiState.Success) {
                    var expandedDepartment by remember { mutableIntStateOf(-1) }

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
                                        item = Item(teacher, item.name),
                                        saved = savedItems.find { it.title == teacher } != null,
                                        onSaveClick = teacherSaved,
                                        onClick = teacherSelected
                                    )
                                }
                                if (state.data.size != index + 1)
                                    HorizontalDivider(Modifier.padding(horizontal = 8.dp))
                            }
                        }
                    }
                }

                if (state is TeachersUiState.Error) ErrorState(
                    refreshAction = { viewModel.refreshTeachers() }
                )
        }
        PullToRefreshContainer(
            modifier = Modifier.align(Alignment.TopCenter),
            state = refreshState
        )
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
    searchQuery: String,
    onQueryChange: (String) -> Unit,
    searchResults: List<Item>,
    itemSelected: (item: Item) -> Unit,
    backAction: (() -> Unit)? = null
) {
    var active by remember { mutableStateOf(false) }
    Row(
        modifier = Modifier.windowInsetsPadding(
            WindowInsets
                .systemBars
                .only(WindowInsetsSides.Horizontal + WindowInsetsSides.Top)
        )
    ) {
        if (backAction != null) Box(
            modifier = Modifier.height(SearchBarDefaults.InputFieldHeight),
            contentAlignment = Alignment.Center
        ) {
            IconButton(onClick = backAction, modifier = Modifier.padding(horizontal = 4.dp)) {
                Icon(
                    imageVector = Icons.AutoMirrored.Default.ArrowBack,
                    contentDescription = stringResource(R.string.description_back)
                )
            }
        }
        DockedSearchBar(
            modifier = Modifier
                .padding(horizontal = 8.dp, vertical = 2.dp),
            query = searchQuery,
            onQueryChange = onQueryChange,
            onSearch = {},
            active = active,
            onActiveChange = { active = it },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = null
                )
            },
            placeholder = { Text("Поиск") }
        ) {
            LazyColumn {
                items(searchResults) {
                    SavedItem(
                        item = it,
                        selected = false,
                        isSaved = true,
                        onClick = { itemSelected(it) }
                    )
                }
            }
        }
    }
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