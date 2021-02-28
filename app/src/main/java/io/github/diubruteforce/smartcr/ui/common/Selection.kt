package io.github.diubruteforce.smartcr.ui.common

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.clickable
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import io.github.diubruteforce.smartcr.model.ui.InputState
import io.github.diubruteforce.smartcr.ui.theme.CornerRadius
import io.github.diubruteforce.smartcr.ui.theme.Margin
import io.github.diubruteforce.smartcr.ui.theme.grayBorder
import io.github.diubruteforce.smartcr.ui.theme.grayText

@Composable
fun CRSelection(
    modifier: Modifier = Modifier,
    state: InputState,
    placeHolder: String,
    icon: ImageVector? = Icons.Outlined.KeyboardArrowDown,
    onClick: () -> Unit
) {
    InputLayout(isError = state.isError, errorText = state.errorText) {
        val interactionState = remember { MutableInteractionSource() }

        Card(
            modifier = modifier.clickable(
                onClick = onClick,
                interactionSource = interactionState,
                indication = null
            ),
            shape = RoundedCornerShape(CornerRadius.normal),
            elevation = 4.dp,
            border = BorderStroke(1.dp, MaterialTheme.colors.grayBorder)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .indication(interactionState, LocalIndication.current)
                    .heightIn(min = 56.dp)
                    .padding(start = Margin.medium, end = Margin.medium),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = placeHolder.take(20),
                    maxLines = 1,
                    style = MaterialTheme.typography.body1,
                    color = MaterialTheme.colors.grayText
                )

                Spacer(modifier = modifier.weight(1f))

                Text(
                    text = state.value,
                    maxLines = 1,
                    style = MaterialTheme.typography.body1
                )

                if (icon != null) {
                    Icon(
                        imageVector = icon,
                        tint = MaterialTheme.colors.primary,
                        contentDescription = null
                    )
                }
            }
        }
    }
}