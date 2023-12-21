package me.paladin.barsurasp.ui.screens

import android.widget.Toast
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import me.paladin.barsurasp.R
import me.paladin.barsurasp.models.BusDirection
import me.paladin.barsurasp.ui.components.CustomToolbar
import me.paladin.barsurasp.ui.components.CustomToolbarScrollBehavior
import me.paladin.barsurasp.ui.components.bus.BusToolbarRow
import me.paladin.barsurasp.ui.components.rememberToolbarScrollBehavior
import me.paladin.barsurasp.ui.viewmodels.BusState
import me.paladin.barsurasp.ui.viewmodels.BusViewModel

@Composable
fun BusInfoScreen(
    number: Int,
    viewModelStoreOwner: ViewModelStoreOwner,
    backAction: () -> Unit,
    stopClicked: (String, Boolean) -> Unit
) {
    val viewModel: BusViewModel = viewModel(
        viewModelStoreOwner = viewModelStoreOwner,
        factory = BusViewModel.Factory(number),
        key = number.toString()
    )

    val busState by viewModel.busState.collectAsState()
    val scrollBehavior = rememberToolbarScrollBehavior()
    var showBackward by rememberSaveable { mutableStateOf(false) }
    val context = LocalContext.current

    Scaffold(
        topBar = {
            BusInfoToolbar(
                direction = viewModel.direction,
                scrollBehavior = scrollBehavior,
                showBackward = showBackward,
                backAction = backAction
            ) { showBackward = !showBackward }
        },
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
    ) { paddingValues ->
        Crossfade(
            modifier = Modifier.padding(paddingValues),
            targetState = busState,
            label = "busInfoScreen"
        ) { state ->
            when (state) {
                is BusState.Loaded -> {
                    val info = state.info
                    val list = if (showBackward) info.backwardStops!! else info.stops

                    LazyColumn {
                        itemsIndexed(list) { index, it ->
                            BusStopItem(name = it.name) {
                                stopClicked(it.name, showBackward)
                            }
                            if (index != list.lastIndex)
                                HorizontalDivider()
                        }
                    }
                }

                is BusState.Loading -> {
                    val progress by viewModel.progress.collectAsState()

                    Column {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                            modifier = Modifier
                                .padding(24.dp)
                                .fillMaxSize()
                        ) {
                            CircularProgressIndicator(
                                progress = { progress },
                                modifier = Modifier.size(96.dp)
                            )
                            Text(text = "Загрузка расписания...")
                        }
                    }
                }

                BusState.None -> {
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
                            text = "Расписание для этого автобуса не загружено. Нажми кнопку ниже чтобы загрузить.",
                            textAlign = TextAlign.Center
                        )
                        Button(
                            onClick = {
                                viewModel.load {
                                    Toast.makeText(
                                        context,
                                        "Ошибка подключения к сети!",
                                        Toast.LENGTH_SHORT
                                    ).show()
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

@Composable
private fun BusInfoToolbar(
    direction: BusDirection,
    showBackward: Boolean,
    scrollBehavior: CustomToolbarScrollBehavior,
    backAction: () -> Unit,
    changeDirection: () -> Unit
) {
    CustomToolbar(
        centralContent = { Text("Остановки") },
        expandedContent = {
            BusToolbarRow(
                name = if (showBackward) direction.backwardName!! else direction.name,
                number = direction.busNumber.toString(),
                changeDirection = if (direction.hasBackward)
                    changeDirection
                else null
            )
        },
        collapsedContent = {
            BusToolbarRow(
                name = if (showBackward) direction.backwardName!! else direction.name,
                number = direction.busNumber.toString()
            )
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

@Composable
private fun BusStopItem(modifier: Modifier = Modifier, name: String, onClick: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .height(54.dp)
            .padding(8.dp)
            .clickable(onClick = onClick)
    ) {
        Text(text = name)
    }
}