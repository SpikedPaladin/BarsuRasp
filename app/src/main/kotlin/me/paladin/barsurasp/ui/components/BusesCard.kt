package me.paladin.barsurasp.ui.components

import android.widget.Toast
import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SecondaryScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import me.paladin.barsurasp.models.BusPath
import me.paladin.barsurasp.ui.viewmodels.BusState
import me.paladin.barsurasp.ui.viewmodels.BusViewModel
import me.paladin.barsurasp.ui.viewmodels.BusesViewModel
import me.paladin.barsurasp.utils.isWeekends

@Composable
fun BusesCard(
    viewModel: BusesViewModel,
    viewModelStoreOwner: ViewModelStoreOwner,
    configClicked: () -> Unit
) {
    val buses by viewModel.paths.collectAsState()
    val selectedPath by viewModel.selectedPath.collectAsState()
    val currentInfo by viewModel.currentInfo.collectAsState()

    ElevatedCard(
        modifier = Modifier
            .animateContentSize()
            .padding(8.dp)
            .padding(bottom = 12.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.padding(start = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Автобусы",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.weight(1F)
                )
                IconButton(onClick = configClicked) {
                    Icon(imageVector = Icons.Outlined.Settings, contentDescription = null)
                }
            }

            HorizontalDivider()

            if (buses.isNotEmpty()) {
                SecondaryScrollableTabRow(
                    selectedTabIndex = selectedPath
                ) {
                    buses.forEachIndexed { index, item ->
                        Tab(selected = true, onClick = { viewModel.selectPath(index) }) {
                            Text(text = item.title, modifier = Modifier.padding(4.dp))
                        }
                    }
                }

                Crossfade(targetState = currentInfo, label = "currentInfo") { info ->
                    BusInfoColumn(info, viewModelStoreOwner)
                }
            }
        }
    }
}

@Composable
private fun BusInfoColumn(
    path: BusPath?,
    viewModelStoreOwner: ViewModelStoreOwner
) {
    if (path == null) return

    Column(Modifier.fillMaxWidth()) {
        for ((number, stop, backward) in path.buses) {
            BusInfoItem(number, stop, backward, viewModelStoreOwner = viewModelStoreOwner)
        }
    }
}

@Composable
private fun BusInfoItem(
    number: Int,
    stop: String,
    backward: Boolean,
    viewModelStoreOwner: ViewModelStoreOwner,
    viewModel: BusViewModel = viewModel(
        viewModelStoreOwner = viewModelStoreOwner,
        factory = BusViewModel.Factory(number),
        key = number.toString()
    )
) {
    val busState by viewModel.busState.collectAsState()
    val context = LocalContext.current

    Column(
        modifier = Modifier.padding(4.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            RoundedBox(Modifier.size(28.dp)) {
                Text(
                    text = number.toString(),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Text(
                text = stop,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(start = 4.dp)
            )
        }

        Crossfade(targetState = busState, label = "mainBusInfo") { state ->
            when (state) {
                is BusState.Loaded -> {
                    val schedule by viewModel.getStopSchedule(stop, backward, 3).collectAsState(null)

                    if (schedule != null) {
                        if (schedule!!.isEmpty()) {
                            Text(text = "Сегодня рейсов больше нет.")
                            return@Crossfade
                        }

                        Row {
                            for ((index, time) in schedule!!.withIndex()) {
                                Text(
                                    text = "${if (time.hour < 10) "0${time.hour}" else time.hour}:" +
                                            "${if (time.minute < 10) "0${time.minute}" else time.minute}",
                                    fontWeight = if (index == 0) FontWeight.Bold else null
                                )

                                if (index != schedule!!.lastIndex)
                                    Spacer(Modifier.size(4.dp))
                            }
                        }
                    } else {
                        Text(text = if (isWeekends()) "Рейсов по выходным нет." else "Рейсов по будням нет.")
                    }
                }
                BusState.Loading -> {
                    val progress by viewModel.progress.collectAsState()
                    Column {
                        Text("Загрузка...")
                        LinearProgressIndicator(progress = { progress })
                    }
                }
                BusState.None -> {
                    Column {
                        Text("Расписание не загружено")
                        Button(
                            onClick = {
                                viewModel.load {
                                    Toast.makeText(context, "Ошибка подключения к сети!", Toast.LENGTH_SHORT).show()
                                }
                            }
                        ) {
                            Text("Загрузить")
                        }
                    }
                }
            }
        }
    }
}