package io.github.diubruteforce.smartcr.ui.common

import androidx.compose.foundation.layout.Row
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import io.github.diubruteforce.smartcr.ui.theme.grayText


@Composable
fun TitleRow(
    title: String,
    icon: ImageVector = Icons.Outlined.Edit,
    onEdit: () -> Unit
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            modifier = Modifier.weight(1f),
            text = title,
            style = MaterialTheme.typography.body1,
            fontWeight = FontWeight.W500,
            color = MaterialTheme.colors.grayText
        )

        IconButton(onClick = onEdit) {
            Icon(
                imageVector = icon,
                tint = MaterialTheme.colors.grayText
            )
        }
    }
}