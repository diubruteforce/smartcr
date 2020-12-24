package io.github.diubruteforce.smartcr.ui.common

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.chrisbanes.accompanist.coil.CoilImage
import io.github.diubruteforce.smartcr.model.data.Teacher
import io.github.diubruteforce.smartcr.ui.theme.CornerRadius
import io.github.diubruteforce.smartcr.ui.theme.Margin
import io.github.diubruteforce.smartcr.ui.theme.SmartCRTheme
import io.github.diubruteforce.smartcr.ui.theme.grayBorder

@Composable
fun TeacherListItem(
    modifier: Modifier = Modifier,
    teacher: Teacher,
    itemClick: (Teacher) -> Unit
) {
    val interactionState = remember { InteractionState() }

    Card(
        modifier = modifier.clickable(
            onClick = { itemClick.invoke(teacher) },
            interactionState = interactionState,
            indication = null
        ),
        shape = RoundedCornerShape(CornerRadius.normal),
        elevation = 8.dp,
        border = BorderStroke(1.dp, MaterialTheme.colors.grayBorder)
    ) {
        Row(
            modifier = Modifier.indication(interactionState, AmbientIndication.current()),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(modifier = Modifier.size(Margin.normal))

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = teacher.fullName,
                    style = MaterialTheme.typography.body1,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1
                )

                Text(
                    text = "Department: ${teacher.departmentCode}",
                    style = MaterialTheme.typography.body2
                )
            }

            Spacer(modifier = Modifier.size(Margin.small))

            Card(
                modifier = Modifier.size(100.dp),
                shape = RoundedCornerShape(CornerRadius.normal),
                elevation = 0.dp,
                backgroundColor = Color.Gray
            ) {
                CoilImage(
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                    data = teacher.profileUrl,
                    fadeIn = true
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewTeacherListItem() {
    SmartCRTheme {
        ScrollableColumn(
            verticalArrangement = Arrangement.spacedBy(Margin.normal),
            contentPadding = PaddingValues(Margin.medium)
        ) {
            (1..10).forEach {
                TeacherListItem(
                    teacher = Teacher(
                        fullName = "Abui mian kuddus boyati",
                        departmentCode = "CSE"
                    ),
                    itemClick = {}
                )
            }
        }
    }
}