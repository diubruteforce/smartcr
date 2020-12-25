package io.github.diubruteforce.smartcr.ui.smartcr.menu

import androidx.annotation.DrawableRes
import androidx.compose.foundation.ScrollableColumn
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
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
    ScrollableColumn(
        modifier = Modifier.navigationBarsPadding(),
        verticalArrangement = Arrangement.spacedBy(Margin.normal),
        contentPadding = PaddingValues(top = Margin.normal, bottom = Margin.large)
    ) {

        Menu.values().toPairList().forEach {
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
    FIND_FACULTY("Find Faculty", R.drawable.find_faculty),
    FIND_COURSE("Find Courses", R.drawable.find_course),
    GET_RESOURCE("Get Resource", R.drawable.find_resource),
    EXTRA_CLASS("Extra Class", R.drawable.extra_class),
    EXAM_ROUTINE("Exam Routine", R.drawable.exam_routine),
    FEES_SCHEDULE("Fees Schedule", R.drawable.fees_schedule),
    YOUR_PROFILE("Your Profile", R.drawable.student_profile),
    APP_SETTING("App Setting", R.drawable.app_setting),
}