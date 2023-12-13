package me.paladin.barsurasp.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import me.paladin.barsurasp.models.BusStop
import me.paladin.barsurasp.ui.components.RoundedBox
import me.paladin.barsurasp.ui.icons.Bus
import me.paladin.barsurasp.ui.icons.Weekends
import me.paladin.barsurasp.ui.icons.Workdays
import me.paladin.barsurasp.ui.viewmodels.BusState
import me.paladin.barsurasp.ui.viewmodels.BusViewModel
import me.paladin.barsurasp.utils.isWeekends

@Composable
fun BusStopScreen(
    number: Int,
    stopName: String,
    backward: Boolean,
    viewModelStoreOwner: ViewModelStoreOwner
) {
    val viewModel: BusViewModel = viewModel(
        viewModelStoreOwner = viewModelStoreOwner,
        factory = BusViewModel.Factory(number),
        key = number.toString()
    )
    val busState by viewModel.busState.collectAsState()

    Scaffold(
        topBar = { BusStopToolbar() }
    ) { paddingValues ->
        Column(
            Modifier
                .padding(paddingValues)
                .padding(8.dp)
        ) {
            if (busState !is BusState.Loaded)
                return@Column

            val info = (busState as BusState.Loaded).info
            val stop = info.getStop(stopName, backward)

            var checked by remember {
                mutableIntStateOf(
                    if (stop.workdays != null) if (isWeekends() && stop.weekends != null) 1 else 0 else 1
                )
            }
            val options = listOf("Будни", "Выходные")
            val icons = listOf(
                Icons.Outlined.Workdays,
                Icons.Outlined.Weekends
            )

            Column(
                Modifier.padding(horizontal = 24.dp, vertical = 4.dp)
            ) {
                SingleChoiceSegmentedButtonRow(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    options.forEachIndexed { index, label ->
                        SegmentedButton(
                            shape = SegmentedButtonDefaults.itemShape(
                                index = index,
                                count = options.size
                            ),
                            icon = {
                                SegmentedButtonDefaults.Icon(active = index == checked) {
                                    Icon(
                                        imageVector = icons[index],
                                        contentDescription = null,
                                        modifier = Modifier.size(SegmentedButtonDefaults.IconSize)
                                    )
                                }
                            },
                            onClick = {
                                checked = index
                            },
                            enabled = (index == 0 && stop.workdays != null) || (index == 1 && stop.weekends != null),
                            selected = checked == index
                        ) {
                            Text(label)
                        }
                    }
                }
                Spacer(Modifier.size(8.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RoundedBox(Modifier.size(48.dp)) {
                        Icon(
                            imageVector = Icons.Outlined.Bus,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    Spacer(Modifier.size(8.dp))
                    Text(text = info.name)
                }
                Spacer(Modifier.size(8.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RoundedBox(Modifier.size(48.dp)) {
                        Icon(
                            imageVector = Icons.Outlined.LocationOn,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    Spacer(Modifier.size(8.dp))
                    Text(text = stopName)
                }
            }

            Text(text = "")
            StopSchedule(list = if (checked == 0) stop.workdays!! else stop.weekends!!)
        }
    }
}

@Composable
private fun StopSchedule(list: List<BusStop.Time>) {
    LazyColumn {
        itemsIndexed(list) {index, item ->
            TimeItem(item = item)

            if (index != list.lastIndex)
                HorizontalDivider()
        }
    }
}

@Composable
private fun TimeItem(item: BusStop.Time) {
    Text(text = "${item.hour}:${item.minute}", modifier = Modifier.height(24.dp))
}

@Composable
private fun BusStopToolbar() {
    TopAppBar(
        title = {
            Text(text = "Расписание")
        }
    )
}