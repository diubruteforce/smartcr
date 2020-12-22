package io.github.diubruteforce.smartcr.ui.common

import androidx.compose.foundation.*
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
import io.github.diubruteforce.smartcr.ui.theme.Margin
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
        val interactionState = remember { InteractionState() }

        Card(
            shape = RoundedCornerShape(16.dp),
            elevation = 4.dp,
            border = BorderStroke(1.dp, MaterialTheme.colors.onBackground.copy(alpha = 0.08f)),
            modifier = modifier.clickable(
                onClick = onClick,
                interactionState = interactionState,
                indication = null
            )
        ) {
            Row(
                modifier = Modifier.fillMaxWidth()
                    .indication(interactionState, AmbientIndication.current())
                    .heightIn(min = 56.dp)
                    .padding(start = Margin.medium, end = Margin.medium),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Text(
                    text = placeHolder.take(20),
                    style = MaterialTheme.typography.body1,
                    color = MaterialTheme.colors.grayText
                )

                Spacer(modifier = modifier.weight(1f))

                Text(
                    text = state.value,
                    style = MaterialTheme.typography.body1
                )

                if (icon != null) {
                    Icon(
                        imageVector = icon,
                        tint = MaterialTheme.colors.primary
                    )
                }
            }
        }
    }
}