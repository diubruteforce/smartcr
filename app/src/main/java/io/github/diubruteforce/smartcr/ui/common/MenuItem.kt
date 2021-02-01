package io.github.diubruteforce.smartcr.ui.common

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import io.github.diubruteforce.smartcr.ui.theme.Margin
import io.github.diubruteforce.smartcr.ui.theme.grayBorder

@Composable
fun MenuItem(
    modifier: Modifier,
    icon: ImageVector,
    title: String,
    onClick: () -> Unit
) {
    val interactionState = remember { InteractionState() }

    Card(
        shape = RoundedCornerShape(16.dp),
        elevation = 4.dp,
        border = BorderStroke(1.dp, MaterialTheme.colors.grayBorder),
        modifier = modifier.clickable(
            onClick = onClick,
            indication = null,
            interactionState = interactionState
        )
    ) {
        Column(
            modifier = Modifier.indication(interactionState, AmbientIndication.current()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                modifier = Modifier
                    .weight(1f)
                    .aspectRatio(1f)
                    .padding(Margin.normal),
                imageVector = icon,
                contentDescription = "Menu Item"
            )

            Text(
                modifier = Modifier
                    .background(MaterialTheme.colors.primary)
                    .fillMaxWidth()
                    .padding(vertical = Margin.small),
                text = title,
                color = MaterialTheme.colors.onPrimary,
                textAlign = TextAlign.Center
            )

            Button(onClick = { /*TODO*/ }) {

            }
        }
    }
}

@Composable
fun MenuRow(
    leftIcon: ImageVector,
    leftTitle: String,
    leftOnClick: () -> Unit,
    rightIcon: ImageVector,
    rightTitle: String,
    rightOnClick: () -> Unit
) {
    Row {
        Spacer(modifier = Modifier.size(Margin.medium))

        MenuItem(
            modifier = Modifier
                .weight(1f)
                .aspectRatio(1f),
            icon = leftIcon,
            title = leftTitle,
            onClick = leftOnClick
        )

        Spacer(modifier = Modifier.size(Margin.medium))

        MenuItem(
            modifier = Modifier
                .weight(1f)
                .aspectRatio(1f),
            icon = rightIcon,
            title = rightTitle,
            onClick = rightOnClick
        )

        Spacer(modifier = Modifier.size(Margin.medium))
    }
}