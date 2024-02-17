package me.paladin.barsurasp.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.ClipOp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import me.paladin.barsurasp.R
import me.paladin.barsurasp.models.BusStop
import me.paladin.barsurasp.models.TimelineElement
import me.paladin.barsurasp.models.TimelineElement.State
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
    val items by viewModel.getTimeline(stopName, backward, workdays, time)
        .collectAsState(initial = listOf())

    Scaffold(
        topBar = {
            BusTimelineToolbar(
                scrollBehavior = scrollBehavior,
                backAction = backAction
            )
        },
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
    ) { paddingValues ->
        LazyColumn(modifier = Modifier.padding(paddingValues)) {
            itemsIndexed(items) { index, item ->
                TimelineItem(
                    element = item,
                    isFirst = index == 0,
                    isLast = index == items.lastIndex
                )
            }
        }
    }
}

@Composable
private fun TimelineItem(element: TimelineElement, isFirst: Boolean, isLast: Boolean) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        TimelineIcon(element.state, isFirst, isLast)
        Text(
            text = element.name,
            modifier = Modifier.weight(1F),
            color = if (element.state == State.PASSED)
                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6F)
            else Color.Unspecified,
            fontWeight = if (element.state == State.ARRIVING)
                FontWeight.Bold
            else null
        )
        Text(
            text = element.time.toString(),
            modifier = Modifier.padding(end = 8.dp),
            color = if (element.state == State.PASSED)
                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6F)
            else Color.Unspecified,
            fontWeight = if (element.state == State.ARRIVING)
                FontWeight.Bold
            else null
        )
    }
}

@Composable
private fun TimelineIcon(state: State, isFirst: Boolean, isLast: Boolean) {
    val color = MaterialTheme.colorScheme.primary

    Box(
        modifier = Modifier
            .size(48.dp)
            .drawBehind {
                val w = size.width
                val h = size.height

                val circle = Path()
                circle.addArc(
                    oval = Rect(
                        left = w / 2 - 8.dp.toPx(),
                        top = w / 2 - 8.dp.toPx(),
                        right = w / 2 + 8.dp.toPx(),
                        bottom = h / 2 + 8.dp.toPx()
                    ),
                    startAngleDegrees = 0F,
                    sweepAngleDegrees = 360F,
                )

                clipPath(
                    path = circle,
                    clipOp = ClipOp.Difference
                ) {
                    if (!isFirst)
                        drawLine(
                            color = if (state == State.PASSED || state == State.ARRIVING)
                                color.copy(alpha = 0.2F)
                            else
                                color.copy(alpha = 0.6F),
                            start = Offset(w / 2, 0F),
                            end = Offset(w / 2, h / 2),
                            strokeWidth = 4.dp.toPx()
                        )

                    if (!isLast)
                        drawLine(
                            color = if (state == State.PASSED)
                                color.copy(alpha = 0.2F)
                            else
                                color.copy(alpha = 0.6F),
                            start = Offset(w / 2, h / 2),
                            end = Offset(w / 2, h),
                            strokeWidth = 4.dp.toPx()
                        )
                }
                drawCircle(
                    color = when (state) {
                        State.PASSED -> color.copy(alpha = 0.2F)
                        else -> color.copy(alpha = 0.6F)
                    },
                    radius = 8.dp.toPx()
                )
                if (state == State.ARRIVING) {
                    drawCircle(
                        color = color,
                        radius = 4.dp.toPx()
                    )
                }
            }
    )
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