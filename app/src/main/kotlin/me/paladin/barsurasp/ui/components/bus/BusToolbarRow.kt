package me.paladin.barsurasp.ui.components.bus

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import me.paladin.barsurasp.R
import me.paladin.barsurasp.ui.components.RoundedBox

@Composable
fun BusToolbarRow(
    name: String,
    number: String,
    changeDirection: (() -> Unit)? = null
) {
    BusToolbarRowLayout(
        startContent = {
            Text(
                text = number,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
        },
        centerContent = { Text(name) },
        endContent = {
            if (changeDirection != null) {
                IconButton(
                    onClick = changeDirection
                ) {
                    Icon(painterResource(R.drawable.ic_direction_switch), contentDescription = null)
                }
            }
        }
    )
}

@Composable
fun BusStopToolbarRow(
    stopName: String
) {
    BusToolbarRowLayout(
        startContent = {
            Icon(
                imageVector = Icons.Outlined.LocationOn,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
        },
        centerContent = { Text(stopName) }
    )
}

@Composable
private fun BusToolbarRowLayout(
    startContent: @Composable () -> Unit,
    centerContent: @Composable () -> Unit,
    endContent: (@Composable () -> Unit)? = null
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        RoundedBox(Modifier.size(48.dp)) {
            startContent()
        }
        Spacer(Modifier.size(8.dp))
        Box(Modifier.weight(1F)) {
            centerContent()
        }

        if (endContent != null)
            endContent()
    }
}