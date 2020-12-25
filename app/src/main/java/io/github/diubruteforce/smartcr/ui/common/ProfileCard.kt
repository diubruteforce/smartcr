package io.github.diubruteforce.smartcr.ui.common

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.diubruteforce.smartcr.R
import io.github.diubruteforce.smartcr.model.data.Student
import io.github.diubruteforce.smartcr.model.data.Teacher
import io.github.diubruteforce.smartcr.ui.theme.CornerRadius
import io.github.diubruteforce.smartcr.ui.theme.Margin
import io.github.diubruteforce.smartcr.ui.theme.SmartCRTheme
import io.github.diubruteforce.smartcr.ui.theme.grayBorder
import java.util.*

@Composable
private fun ProfileCard(
    modifier: Modifier = Modifier,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    content: @Composable ColumnScope.() -> Unit,
    sideContent: @Composable BoxScope.() -> Unit
) {
    Box(modifier = modifier) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(CornerRadius.normal),
            elevation = 4.dp,
            border = BorderStroke(1.dp, MaterialTheme.colors.grayBorder)
        ) {
            Column(
                modifier = Modifier.padding(Margin.normal),
                verticalArrangement = Arrangement.spacedBy(Margin.small),
                content = content
            )
        }

        Box(
            modifier = Modifier.align(Alignment.CenterEnd),
            content = sideContent
        )

        UpdateDeleteMenu(
            modifier = Modifier.align(Alignment.TopEnd),
            onEdit = onEdit,
            onDelete = onDelete
        )
    }
}

@Composable
fun TeacherProfileCard(
    modifier: Modifier = Modifier,
    teacher: Teacher,
    onCall: (String) -> Unit,
    onEdit: (String) -> Unit,
    onDelete: (String) -> Unit
) {
    ProfileCard(
        modifier = modifier,
        onEdit = { onEdit.invoke(teacher.id) },
        onDelete = { onDelete.invoke(teacher.id) },
        content = {
            LabelText(
                label = stringResource(id = R.string.teacher_initial),
                text = teacher.initial
            )

            LabelText(
                label = stringResource(id = R.string.department),
                text = teacher.departmentCode
            )

            LabelText(
                label = stringResource(id = R.string.designation),
                text = teacher.designation
            )

            LabelText(
                label = stringResource(id = R.string.diu_email),
                text = teacher.diuEmail
            )

            LabelText(
                label = stringResource(id = R.string.room),
                text = teacher.room
            )

            LabelText(
                label = stringResource(id = R.string.phone),
                text = teacher.phone
            )
        },
        sideContent = {
            if (teacher.phone.isNotEmpty()) {
                OutlinedButton(
                    shape = RoundedCornerShape(
                        topLeft = 16.dp,
                        topRight = 0.dp,
                        bottomRight = 0.dp,
                        bottomLeft = 16.dp
                    ),
                    onClick = { onCall.invoke(teacher.phone) }
                ) {
                    Text(text = stringResource(id = R.string.call).toUpperCase(Locale.getDefault()))
                }
            }
        }
    )
}

@Composable
fun StudentProfileCard(
    modifier: Modifier = Modifier,
    student: Student,
    onEdit: (String) -> Unit,
    onDelete: (String) -> Unit
) {
    ProfileCard(
        modifier = modifier,
        onEdit = { onEdit.invoke(student.id) },
        onDelete = { onDelete.invoke(student.id) },
        content = {
            LabelText(
                label = stringResource(id = R.string.department),
                text = student.departmentCode
            )

            LabelText(
                label = stringResource(id = R.string.student_id),
                text = student.diuId
            )

            LabelText(
                label = stringResource(id = R.string.diu_email),
                text = student.diuEmail
            )

            LabelText(
                label = stringResource(id = R.string.phone),
                text = student.phone
            )

            LabelText(
                label = stringResource(id = R.string.gender),
                text = student.gender
            )

            LabelText(
                label = stringResource(id = R.string.term),
                text = student.term
            )

            LabelText(
                label = stringResource(id = R.string.level),
                text = student.level
            )
        },
        sideContent = {
            OutlinedButton(
                shape = RoundedCornerShape(
                    topLeft = 16.dp,
                    topRight = 0.dp,
                    bottomRight = 0.dp,
                    bottomLeft = 16.dp
                ),
                onClick = { }
            ) {
                Text(text = "BATCH ${student.batch}")
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
private fun PreviewTeacherProfileCard() {
    SmartCRTheme {
        TeacherProfileCard(
            modifier = Modifier.padding(Margin.normal),
            teacher = Teacher().copy(phone = "8787384793247"),
            onCall = { /*TODO*/ },
            onEdit = { /*TODO*/ },
            onDelete = { /*TODO*/ }
        )
    }
}