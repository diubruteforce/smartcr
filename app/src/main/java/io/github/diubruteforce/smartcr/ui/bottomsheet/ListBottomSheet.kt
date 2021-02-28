package io.github.diubruteforce.smartcr.ui.bottomsheet

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import io.github.diubruteforce.smartcr.ui.theme.Margin

@Composable
fun <T> ListBottomSheet(
    modifier: Modifier = Modifier,
    title: String,
    imageVector: ImageVector,
    onClose: () -> Unit,
    list: List<T>,
    onItemClick: (T) -> Unit
) {
    ListBottomSheet(
        modifier = modifier,
        title = title,
        icon = rememberVectorPainter(image = imageVector),
        onClose = onClose,
        list = list,
        onItemClick = onItemClick
    )
}

@Composable
fun <T> ListBottomSheet(
    modifier: Modifier = Modifier,
    title: String,
    icon: Painter,
    onClose: () -> Unit,
    list: List<T>,
    onItemClick: (T) -> Unit
) {
    Column(modifier = modifier) {
        SheetHeader(title = title, icon = icon, onClose = onClose)

        LazyColumn {
            items(list) {
                SheetListItem(name = it.toString(), onSelected = { onItemClick.invoke(it) })
            }

            item {
                Spacer(modifier = Modifier.height(Margin.inset))
            }
        }
    }
}