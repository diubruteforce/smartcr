package io.github.diubruteforce.smartcr.ui.bottomsheet

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Clear
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.unit.dp
import io.github.diubruteforce.smartcr.ui.theme.IconSize
import io.github.diubruteforce.smartcr.ui.theme.Margin

@Composable
fun ColumnScope.SheetHeader(title: String, imageVector: ImageVector, onClose: () -> Unit) {
    SheetHeader(title = title, icon = rememberVectorPainter(imageVector), onClose = onClose)
}

@Composable
fun ColumnScope.SheetHeader(title: String, icon: Painter, onClose: () -> Unit) {
    IconButton(
        onClick = onClose,
        modifier = Modifier.align(Alignment.End)
    ) {
        Icon(
            imageVector = Icons.Outlined.Clear,
            contentDescription = "SmartCR"
        )
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Margin.normal),
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = Margin.medium, bottom = Margin.normal)
    ) {
        Card(
            modifier = Modifier.size(40.dp),
            elevation = 0.dp,
            shape = RoundedCornerShape(20.dp),
            backgroundColor = MaterialTheme.colors.primary.copy(alpha = 0.1f)
        ) {
            Icon(
                painter = icon,
                modifier = Modifier.size(IconSize.normal),
                tint = MaterialTheme.colors.primary,
                contentDescription = "Sheet Header Icon"
            )
        }

        Text(
            text = title,
            style = MaterialTheme.typography.h5
        )
    }
}