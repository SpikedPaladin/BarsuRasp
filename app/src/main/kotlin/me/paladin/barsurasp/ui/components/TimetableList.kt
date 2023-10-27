package me.paladin.barsurasp.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import me.paladin.barsurasp.models.Timetable
import me.paladin.barsurasp.utils.getCurrentApiDate

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TimetableList(timetable: Timetable) {
    val pagerState = rememberPagerState(pageCount = {
        timetable.days.size
    }, initialPage = timetable.getDayNumberFromDate(getCurrentApiDate()))

    HorizontalPager(
        state = pagerState,
        beyondBoundsPageCount = 7,
        pageSpacing = 8.dp,
        contentPadding = PaddingValues(20.dp)
    ) { page ->
        TimetablePage(day = timetable.days[page])
    }
}