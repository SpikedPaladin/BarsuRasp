package me.paladin.barsurasp.ui.components.barsu

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import me.paladin.barsurasp.models.DaySchedule
import me.paladin.barsurasp.utils.readableDay

@Composable
fun PageTitle(day: DaySchedule) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = stringResource(readableDay(day.dayOfWeek)),
            style = MaterialTheme.typography.headlineSmall
        )
        Text(
            text = day.date,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}