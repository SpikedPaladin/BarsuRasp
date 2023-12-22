package me.paladin.barsurasp.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import me.paladin.barsurasp.R
import me.paladin.barsurasp.models.BusPath
import me.paladin.barsurasp.ui.components.RoundedBox
import me.paladin.barsurasp.ui.icons.Bus
import me.paladin.barsurasp.ui.viewmodels.BusPathViewModel

@Composable
fun BusPathScreen(
    busPathViewModel: BusPathViewModel,
    openBusChoose: () -> Unit,
    backAction: () -> Unit,
    successCallback: (BusPath) -> Unit
) {
    val title by busPathViewModel.title.collectAsState()
    val buses by busPathViewModel.buses.collectAsState()

    Scaffold(
        topBar = { BusPathToolbar(backAction) }
    ) { paddingValues ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(12.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.Bus,
                    contentDescription = null,
                    modifier = Modifier.size(96.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            OutlinedTextField(
                value = title,
                onValueChange = { busPathViewModel.setTitle(it) },
                label = { Text(text = "Название маршрута") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.size(8.dp))

            Text(
                text = "Автобусы",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(Modifier.size(8.dp))

            LazyColumn {
                items(buses) {
                    PathItem(number = it.first, stopName = it.second)
                }

                item {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(6.dp)
                            .clickable { openBusChoose() }
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Add,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(36.dp)
                        )
                        Text(
                            text = "Добавить",
                            color = MaterialTheme.colorScheme.primary,
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }
            }

            Button(
                onClick = {
                    successCallback(BusPath(title = title, buses = buses))
                },
                enabled = title != "" && buses.isNotEmpty()
            ) {
                Text(text = "Сохранить")
            }
        }
    }
}

@Composable
private fun PathItem(number: Int, stopName: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.height(36.dp)
    ) {
        RoundedBox(Modifier.size(32.dp)) {
            Text(
                text = number.toString(),
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.titleMedium
            )
        }
        Text(
            text = stopName,
            modifier = Modifier.padding(start = 4.dp).weight(1F)
        )
    }
}

@Composable
private fun BusPathToolbar(backAction: () -> Unit) {
    TopAppBar(
        title = {
            Text(text = "Новый маршрут")
        },
        navigationIcon = {
            IconButton(onClick = backAction) {
                Icon(
                    imageVector = Icons.AutoMirrored.Default.ArrowBack,
                    contentDescription = stringResource(R.string.description_back)
                )
            }
        }
    )
}