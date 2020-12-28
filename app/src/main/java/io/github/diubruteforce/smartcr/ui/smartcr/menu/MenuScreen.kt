package io.github.diubruteforce.smartcr.ui.smartcr.menu

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.vectorResource
import dev.chrisbanes.accompanist.insets.navigationBarsPadding
import io.github.diubruteforce.smartcr.R
import io.github.diubruteforce.smartcr.ui.common.MenuRow
import io.github.diubruteforce.smartcr.ui.theme.Margin
import io.github.diubruteforce.smartcr.utils.extension.toPairList

@Composable
fun MenuScreen(
    onMenuClick: (Menu) -> Unit
) {
    LazyColumn(
        modifier = Modifier.navigationBarsPadding(),
        verticalArrangement = Arrangement.spacedBy(Margin.normal),
        contentPadding = PaddingValues(top = Margin.normal, bottom = Margin.large)
    ) {
        items(Menu.values().toPairList()) {
            MenuRow(
                leftIcon = vectorResource(id = it.first.iconRes),
                leftTitle = it.first.title,
                leftOnClick = { onMenuClick(it.first) },
                rightIcon = vectorResource(id = it.second.iconRes),
                rightTitle = it.second.title,
                rightOnClick = { onMenuClick(it.second) }
            )
        }
    }
}

enum class Menu(val title: String, @DrawableRes val iconRes: Int) {
    FIND_FACULTY("Find Teacher", R.drawable.find_faculty),
    FIND_COURSE("Find Courses", R.drawable.find_course),
    Event("Event", R.drawable.event),
    EXAM_ROUTINE("Exam Routine", R.drawable.exam_routine),
    FEES_SCHEDULE("Fees Schedule", R.drawable.fees_schedule),
    APP_SETTING("App Setting", R.drawable.app_setting),
}