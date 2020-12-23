package io.github.diubruteforce.smartcr.ui.bottomsheet

import androidx.compose.foundation.ScrollableColumn
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import dev.chrisbanes.accompanist.insets.navigationBarsPadding

@Composable
fun <T> ListBottomSheet(
    modifier: Modifier = Modifier,
    title: String,
    icon: ImageVector,
    onClose: () -> Unit,
    list: List<T>,
    onItemClick: (T) -> Unit
) {
    Column(modifier = modifier.navigationBarsPadding()) {
        SheetHeader(title = title, icon = icon, onClose = onClose)

        ScrollableColumn {
            list.forEach {
                SheetListItem(name = it.toString(), onSelected = { onItemClick.invoke(it) })
            }
        }
    }
}