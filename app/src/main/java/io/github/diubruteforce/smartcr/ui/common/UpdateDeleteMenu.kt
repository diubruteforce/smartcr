package io.github.diubruteforce.smartcr.ui.common

import androidx.compose.foundation.layout.Box
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.MoreHoriz
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import io.github.diubruteforce.smartcr.R

@Composable
fun UpdateDeleteMenu(
    modifier: Modifier = Modifier,
    iconColor: Color = LocalContentColor.current.copy(alpha = LocalContentAlpha.current),
    icon: ImageVector = Icons.Outlined.MoreHoriz,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    var isExpanded by remember { mutableStateOf(false) }
    Box(modifier = modifier) {
        IconButton(onClick = { isExpanded = true }) {
            Icon(
                imageVector = icon,
                tint = iconColor,
                contentDescription = "Menu"
            )
        }

        DropdownMenu(
            expanded = isExpanded,
            onDismissRequest = { isExpanded = false }
        ) {
            DropdownMenuItem(onClick = {
                onEdit.invoke()
                isExpanded = false
            }) {
                Text(text = stringResource(id = R.string.edit))
            }

            DropdownMenuItem(onClick = {
                onDelete.invoke()
                isExpanded = false
            }) {
                Text(text = stringResource(id = R.string.delete))
            }
        }
    }
}