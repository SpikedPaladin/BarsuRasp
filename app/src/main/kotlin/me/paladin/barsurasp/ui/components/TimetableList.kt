package me.paladin.barsurasp.ui.components

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import me.paladin.barsurasp.models.Timetable
import me.paladin.barsurasp.utils.getCurrentApiDate

@Composable
fun TimetableList(timetable: Timetable) {
    val pagerState = rememberPagerState(
        pageCount = { timetable.days.size },
        initialPage = timetable.getDayNumberFromDate(getCurrentApiDate())
    )

    HorizontalPager(
        modifier = Modifier.animateContentSize(),
        contentPadding = PaddingValues(
            bottom = 8.dp,
            top = 4.dp,
            start = 20.dp,
            end = 20.dp
        ),
        verticalAlignment = Alignment.Top,
        pageSpacing = 8.dp,
        state = pagerState
    ) { page ->
        TimetablePage(day = timetable.days[page])
    }
}