package io.github.diubruteforce.smartcr.ui.common

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.KeyboardArrowRight
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import io.github.diubruteforce.smartcr.ui.theme.Margin
import io.github.diubruteforce.smartcr.ui.theme.grayText

@Composable
fun CRSelection(
    modifier: Modifier = Modifier,
    text: String?,
    placeHolder: String,
    icon: ImageVector? = Icons.Outlined.KeyboardArrowRight,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        elevation = 4.dp,
        border = BorderStroke(1.dp, MaterialTheme.colors.onBackground.copy(alpha = 0.08f)),
        modifier = modifier.clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth()
                .heightIn(min = 56.dp)
                .padding(start = Margin.medium, end = Margin.medium),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (text != null) {
                Text(
                    text = text,
                    style = MaterialTheme.typography.body1
                )
            } else {
                Text(
                    text = placeHolder,
                    style = MaterialTheme.typography.body1,
                    color = MaterialTheme.colors.grayText
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            if (icon != null) {
                Icon(
                    imageVector = icon,
                    tint = MaterialTheme.colors.primary
                )
            }
        }
    }
}