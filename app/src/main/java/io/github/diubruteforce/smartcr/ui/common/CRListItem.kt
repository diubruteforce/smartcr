package io.github.diubruteforce.smartcr.ui.common

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.clickable
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.KeyboardArrowRight
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.diubruteforce.smartcr.ui.theme.CornerRadius
import io.github.diubruteforce.smartcr.ui.theme.Margin
import io.github.diubruteforce.smartcr.ui.theme.grayBorder
import io.github.diubruteforce.smartcr.ui.theme.grayText

@Composable
fun CRListItem(
    modifier: Modifier = Modifier,
    text: String,
    itemClick: () -> Unit
) {
    val interactionState = remember { MutableInteractionSource() }

    Card(
        modifier = modifier.clickable(
            onClick = itemClick,
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
                modifier = Modifier.weight(1f),
                text = text,
                maxLines = 1,
                style = MaterialTheme.typography.body1,
                color = MaterialTheme.colors.grayText
            )

            Icon(
                imageVector = Icons.Outlined.KeyboardArrowRight,
                tint = MaterialTheme.colors.primary,
                contentDescription = null
            )
        }
    }
}