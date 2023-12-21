package me.paladin.barsurasp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import me.paladin.barsurasp.R
import me.paladin.barsurasp.models.BusInfo
import me.paladin.barsurasp.models.BusStop
import me.paladin.barsurasp.models.BusStop.Companion.hourSorted
import me.paladin.barsurasp.ui.components.CustomToolbar
import me.paladin.barsurasp.ui.components.CustomToolbarScrollBehavior
import me.paladin.barsurasp.ui.components.bus.BusStopToolbarRow
import me.paladin.barsurasp.ui.components.bus.BusToolbarRow
import me.paladin.barsurasp.ui.components.bus.WeekDaysSelector
import me.paladin.barsurasp.ui.components.rememberToolbarScrollBehavior
import me.paladin.barsurasp.ui.viewmodels.BusState
import me.paladin.barsurasp.ui.viewmodels.BusViewModel
import me.paladin.barsurasp.utils.isWeekends

@Composable
fun BusStopScreen(
    number: Int,
    stopName: String,
    backward: Boolean,
    viewModelStoreOwner: ViewModelStoreOwner,
    backAction: () -> Unit
) {
    val viewModel: BusViewModel = viewModel(
        viewModelStoreOwner = viewModelStoreOwner,
        factory = BusViewModel.Factory(number),
        key = number.toString()
    )

    val scrollBehavior = rememberToolbarScrollBehavior()
    val busState by viewModel.busState.collectAsState()
    val info = (busState as BusState.Loaded).info
    val stop = info.getStop(stopName, backward)
    val nearestTime by viewModel.getStopSchedule(stopName, backward, 1).collectAsState(null)
    var checked by remember {
        mutableIntStateOf(
            if (stop.hasWorkdays) if (isWeekends() && stop.hasWeekends) 1 else 0 else 1
        )
    }

    Scaffold(
        topBar = {
            BusStopToolbar(
                info = info,
                stop = stop,
                checked = checked,
                backward = backward,
                scrollBehavior,
                backAction = backAction
            ) {
                checked = it
            }
        },
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
    ) { paddingValues ->
        val list = if (checked == 0) stop.workdays!!.hourSorted() else stop.weekends!!.hourSorted()

        LazyColumn(
            modifier = Modifier
                .padding(paddingValues)
        ) {
            itemsIndexed(list) { index, item ->
                TimeItem(item = item, nearestTime = if (nearestTime != null) nearestTime!!.firstOrNull() else null)

                if (index != list.lastIndex)
                    HorizontalDivider()
            }
        }
    }
}

@Composable
private fun TimeItem(item: List<BusStop.Time>, nearestTime: BusStop.Time?) {
    Row(
        modifier = Modifier
            .height(42.dp)
            .padding(start = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        for (time in item) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(vertical = 6.dp)
                    .let {
                        if (time == nearestTime)
                            return@let it
                                .clip(MaterialTheme.shapes.medium)
                                .background(MaterialTheme.colorScheme.primary.copy(0.2F))

                        it
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = time.toString(),
                    modifier = Modifier.padding(horizontal = 6.dp),
                    color = if (time == nearestTime)
                        MaterialTheme.colorScheme.primary
                    else Color.Unspecified,
                    fontWeight = if (time == nearestTime)
                        FontWeight.Bold
                    else null
                )
            }
        }
    }
}

@Composable
private fun BusStopToolbar(
    info: BusInfo,
    stop: BusStop,
    checked: Int,
    backward: Boolean,
    scrollBehavior: CustomToolbarScrollBehavior,
    backAction: () -> Unit,
    onChecked: (Int) -> Unit
) {
    val localDensity = LocalDensity.current
    var selectorHeightDp by remember { mutableStateOf(0.dp) }

    CustomToolbar(
        centralContent = { Text("Расписание") },
        expandedContent = {
            Column {
                WeekDaysSelector(
                    modifier = Modifier.onGloballyPositioned { coordinates ->
                        selectorHeightDp = with(localDensity) { coordinates.size.height.toDp() }
                    },
                    hasWorkdays = stop.hasWorkdays,
                    hasWeekends = stop.hasWeekends,
                    checked = checked,
                    onChecked = onChecked
                )
                Spacer(Modifier.size(8.dp))
                BusToolbarRow(name = if (backward) info.backwardName!! else info.name, number = info.number.toString())
                Spacer(Modifier.size(8.dp))
                BusStopToolbarRow(stopName = stop.name)
            }
        },
        collapsedContent = {
             Column {
                 Spacer(Modifier.size((selectorHeightDp + 8.dp) * (1 - scrollBehavior.state.collapsedFraction)))
                 BusToolbarRow(name = stop.name, number = info.number.toString())
             }
        },
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