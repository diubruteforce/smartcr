package io.github.diubruteforce.smartcr.ui.smartcr.menu

import androidx.compose.foundation.ScrollableColumn
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Pages
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import dev.chrisbanes.accompanist.insets.navigationBarsPadding
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
                leftIcon = it.first.imageVector,
                leftTitle = it.first.title,
                leftOnClick = { onMenuClick(it.first) },
                rightIcon = it.second.imageVector,
                rightTitle = it.second.title,
                rightOnClick = { onMenuClick(it.second) }
            )
        }
    }
}

enum class Menu(val title: String, val imageVector: ImageVector) {
    FIND_FACULTY("Find Faculty", Icons.Outlined.Pages),
    FIND_COURSE("Find Courses", Icons.Outlined.Pages),
    GET_RESOURCE("Get Resource", Icons.Outlined.Pages),
    EXTRA_CLASS("Extra Class", Icons.Outlined.Pages),
    EXAM_ROUTINE("Exam Routine", Icons.Outlined.Pages),
    FEES_SCHEDULE("Fees Schedule", Icons.Outlined.Pages),
    YOUR_PROFILE("Your Profile", Icons.Outlined.Pages),
    APP_SETTING("App Setting", Icons.Outlined.Pages),
}