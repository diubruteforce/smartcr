package io.github.diubruteforce.smartcr.ui.smartcr.menu

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import io.github.diubruteforce.smartcr.R
import io.github.diubruteforce.smartcr.ui.common.MenuRow
import io.github.diubruteforce.smartcr.ui.theme.Margin
import io.github.diubruteforce.smartcr.utils.extension.toPairList

@Composable
fun MenuScreen(
    onMenuClick: (Menu) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceAround,
        contentPadding = PaddingValues(top = Margin.normal)
    ) {
        items(Menu.values().toPairList()) {
            MenuRow(
                leftIcon = painterResource(id = it.first.iconRes),
                leftTitle = it.first.title,
                leftOnClick = { onMenuClick(it.first) },
                rightIcon = painterResource(id = it.second.iconRes),
                rightTitle = it.second.title,
                rightOnClick = { onMenuClick(it.second) }
            )
        }

        item { Spacer(modifier = Modifier.height(Margin.inset)) }
    }
}

enum class Menu(val title: String, @DrawableRes val iconRes: Int) {
    FIND_FACULTY("Find Teacher", R.drawable.find_faculty),
    FIND_COURSE("Find Courses", R.drawable.find_course),
    Event("Event", R.drawable.event),
    EXAM_ROUTINE("Exam Routine", R.drawable.exam_routine),
    FEES_SCHEDULE("Fees Schedule", R.drawable.fees_schedule),
    ABOUT_APP("About App", R.drawable.app_setting),
}