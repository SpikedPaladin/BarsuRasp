package me.paladin.barsurasp.ui.components.bus

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Icon
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import me.paladin.barsurasp.ui.icons.Weekends
import me.paladin.barsurasp.ui.icons.Workdays

@Composable
fun WeekDaysSelector(
    modifier: Modifier = Modifier,
    hasWorkdays: Boolean,
    hasWeekends: Boolean,
    checked: Int,
    onChecked: (Int) -> Unit
) {
    val options = listOf("Будни", "Выходные")
    val icons = listOf(
        Icons.Outlined.Workdays,
        Icons.Outlined.Weekends
    )

    SingleChoiceSegmentedButtonRow(
        modifier = modifier
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
                    onChecked(index)
                },
                enabled = (index == 0 && hasWorkdays) || (index == 1 && hasWeekends),
                selected = checked == index
            ) {
                Text(label)
            }
        }
    }
}