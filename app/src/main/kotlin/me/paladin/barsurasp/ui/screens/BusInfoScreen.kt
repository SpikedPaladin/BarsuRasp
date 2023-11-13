package me.paladin.barsurasp.ui.screens

import android.widget.Toast
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import me.paladin.barsurasp.ui.components.RoundedBox
import me.paladin.barsurasp.ui.icons.Bus
import me.paladin.barsurasp.ui.viewmodels.BusState
import me.paladin.barsurasp.ui.viewmodels.BusViewModel

@Composable
fun BusInfoScreen(
    number: Int,
    viewModelStoreOwner: ViewModelStoreOwner,
    stopClicked: (String, Boolean) -> Unit
) {
    val viewModel: BusViewModel = viewModel(
        viewModelStoreOwner = viewModelStoreOwner,
        factory = BusViewModel.Factory(number),
        key = number.toString()
    )

    val busState by viewModel.busState.collectAsState()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val context = LocalContext.current

    Scaffold(
        topBar = { BusInfoToolbar(number, scrollBehavior) },
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
    ) { paddingValues ->
        Column(
            Modifier
                .padding(paddingValues)
                .padding(8.dp)
        ) {
            Crossfade(targetState = busState, label = "busInfoScreen") { state ->
                when (state) {
                    is BusState.Loaded -> {
                        var showBackward by remember { mutableStateOf(false) }
                        val info = state.info

                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                RoundedBox(Modifier.size(48.dp)) {
                                    Icon(
                                        imageVector = Icons.Outlined.Bus,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }
                                Spacer(Modifier.size(8.dp))
                                Text(text = if (showBackward) info.backwardName!! else info.name)

                                if (info.hasBackward) {
                                    IconButton(
                                        modifier = Modifier.weight(1F),
                                        onClick = { showBackward = !showBackward }
                                    ) {
                                        Icon(imageVector = Icons.Default.ArrowDropDown, contentDescription = null)
                                    }
                                }
                            }
                            LazyColumn {
                                items(if (showBackward) info.backwardStops!! else info.stops) {
                                    Row(
                                        Modifier
                                            .height(48.dp)
                                            .clickable { stopClicked(it.name, showBackward) }) {
                                        Text(text = it.name)
                                    }
                                }
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
                                    progress = progress,
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
                            Text(text = "Расписание для этого автобуса не загружено. Нажми кнопку ниже чтобы загрузить.", textAlign = TextAlign.Center)
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
}

@Composable
fun BusInfoToolbar(number: Int, scrollBehavior: TopAppBarScrollBehavior) {
    TopAppBar(
        title = {
            Text(text = "Автобус №$number")
        },
        scrollBehavior = scrollBehavior
    )
}