package io.github.diubruteforce.smartcr.ui.bottomsheet

import androidx.compose.foundation.ScrollableColumn
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import io.github.diubruteforce.smartcr.ui.theme.Margin

@Composable
fun <T> ListBottomSheet(
    modifier: Modifier = Modifier,
    title: String,
    icon: ImageVector,
    onClose: () -> Unit,
    list: List<T>,
    onItemClick: (T) -> Unit
) {
    Column(modifier = modifier) {
        SheetHeader(title = title, icon = icon, onClose = onClose)

        ScrollableColumn {
            list.forEach {
                SheetListItem(name = it.toString(), onSelected = { onItemClick.invoke(it) })
            }

            Spacer(modifier = Modifier.height(Margin.inset))
        }
    }
}