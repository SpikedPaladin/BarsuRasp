package me.paladin.barsurasp.glance.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.ColorFilter
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.LocalContext
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.padding
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import me.paladin.barsurasp.R
import me.paladin.barsurasp.glance.actions.NextDayActionCallback
import me.paladin.barsurasp.glance.actions.PreviousDayActionCallback
import me.paladin.barsurasp.ui.MainActivity
import me.paladin.barsurasp.utils.getApiDay

@Composable
fun GlanceTimetableHeader(
    title: String,
    date: String
) {
    Row(
        modifier = GlanceModifier.fillMaxWidth().padding(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            style = TextStyle(color = GlanceTheme.colors.onBackground, fontSize = 20.sp),
            modifier = GlanceModifier.defaultWeight().clickable(actionStartActivity<MainActivity>()),
            text = title
        )

        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(
                modifier = GlanceModifier.clickable(onClick = actionRunCallback<PreviousDayActionCallback>()),
                provider = ImageProvider(R.drawable.ic_arrow_left),
                contentDescription = LocalContext.current.getString(R.string.description_prev_day),
                colorFilter = ColorFilter.tint(GlanceTheme.colors.primary)
            )
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = getApiDay(date),
                    style = TextStyle(
                        color = GlanceTheme.colors.onBackground,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                )
                Text(text = date, style = TextStyle(color = GlanceTheme.colors.onBackground, fontSize = 12.sp))
            }
            Image(
                modifier = GlanceModifier.clickable(onClick = actionRunCallback<NextDayActionCallback>()),
                provider = ImageProvider(R.drawable.ic_arrow_right),
                contentDescription = LocalContext.current.getString(R.string.description_next_day),
                colorFilter = ColorFilter.tint(GlanceTheme.colors.primary)
            )
        }
    }
}