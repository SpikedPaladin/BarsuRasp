package me.paladin.barsurasp.ui.components.barsu

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import me.paladin.barsurasp.utils.formatWeek
import me.paladin.barsurasp.utils.getCurrentWeek

@Composable
fun WeekSelector(
    modifier: Modifier = Modifier,
    week: String,
    prevClicked: () -> Unit,
    nextClicked: () -> Unit,
    weekClicked: () -> Unit
) {
    SingleChoiceSegmentedButtonRow(
        modifier = modifier
    ) {
        SegmentedButton(
            onClick = prevClicked,
            selected = false,
            shape = SegmentedButtonDefaults.itemShape(index = 0, count = 3)
        ) {
            Icon(imageVector = Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = null)
        }
        SegmentedButton(
            onClick = weekClicked,
            selected = week == getCurrentWeek(),
            shape = SegmentedButtonDefaults.itemShape(index = 1, count = 3)
        ) {
            Text(formatWeek(week))
        }
        SegmentedButton(
            onClick = nextClicked,
            selected = false,
            shape = SegmentedButtonDefaults.itemShape(index = 2, count = 3)
        ) {
            Icon(imageVector = Icons.AutoMirrored.Outlined.ArrowForward, contentDescription = null)
        }
    }
}

@Preview
@Composable
private fun WeekSelectorPreview() {
    WeekSelector(
        week = "11.12.2023",
        prevClicked = {},
        nextClicked = {},
        weekClicked = {}
    )
}