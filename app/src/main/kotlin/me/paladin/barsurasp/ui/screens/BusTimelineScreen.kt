package me.paladin.barsurasp.ui.screens

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import me.paladin.barsurasp.R
import me.paladin.barsurasp.models.BusStop
import me.paladin.barsurasp.models.TimelineElement
import me.paladin.barsurasp.ui.viewmodels.BusViewModel

@Composable
fun BusTimelineScreen(
    number: Int,
    stopName: String,
    backward: Boolean,
    workdays: Boolean,
    time: BusStop.Time,
    viewModelStoreOwner: ViewModelStoreOwner,
    backAction: () -> Unit
) {
    val viewModel: BusViewModel = viewModel(
        viewModelStoreOwner = viewModelStoreOwner,
        factory = BusViewModel.Factory(number),
        key = number.toString()
    )

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    val items by viewModel.getTimeline(stopName, backward, workdays, time).collectAsState(initial = listOf())

    Scaffold(
        topBar = {
            BusTimelineToolbar(
                scrollBehavior = scrollBehavior,
                backAction = backAction
            )
        },
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
    ) { paddingValues ->
        // Arriving dot indicator
        // Not arriving gradient
        LazyColumn(modifier = Modifier.padding(paddingValues)) {
            items(items) {
                TimelineItem(element = it)
            }
        }
    }
}

@Composable
private fun TimelineItem(element: TimelineElement) {
    Row {
        Text(text = element.state.toString())
        Text(
            text = element.name,
            modifier = Modifier
                .weight(1F)
        )
        Text(text = element.time.toString())
    }
}

@Composable
private fun BusTimelineToolbar(
    scrollBehavior: TopAppBarScrollBehavior,
    backAction: () -> Unit
) {
    TopAppBar(
        title = { Text("Таймлайн") },
        navigationIcon = {
            IconButton(onClick = backAction) {
                Icon(
                    imageVector = Icons.AutoMirrored.Default.ArrowBack,
                    contentDescription = stringResource(R.string.description_back)
                )
            }
        },
        scrollBehavior = scrollBehavior
    )
}