package me.paladin.barsurasp.ui.components.barsu

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import me.paladin.barsurasp.models.Timetable
import me.paladin.barsurasp.utils.getCurrentApiDate

@Composable
fun TimetableList(
    timetable: Timetable,
    openTeacher: (String) -> Unit
) {
    val pagerState = rememberPagerState(
        pageCount = { timetable.pageCount },
        initialPage = timetable.getDayNumberFromDate(getCurrentApiDate())
    )

    HorizontalPager(
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
        if (timetable is Timetable.Group)
            TimetablePage(day = timetable.days[page], openTeacher = openTeacher)
        if (timetable is Timetable.Teacher)
            TimetablePage(day = timetable.days[page], openTeacher = openTeacher)
    }
}