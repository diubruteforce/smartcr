package io.github.diubruteforce.smartcr.ui.common

import android.net.Uri
import androidx.compose.animation.animate
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ScrollableColumn
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.diubruteforce.smartcr.model.data.Course
import io.github.diubruteforce.smartcr.model.data.Instructor
import io.github.diubruteforce.smartcr.model.data.Resource
import io.github.diubruteforce.smartcr.ui.theme.*
import io.github.diubruteforce.smartcr.utils.extension.lighten
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun ResourceListItem(
    modifier: Modifier = Modifier,
    resource: Resource,
    progressType: ProgressType,
    onClick: () -> Unit,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(CornerRadius.normal),
        elevation = 4.dp,
        border = BorderStroke(1.dp, MaterialTheme.colors.grayBorder)
    ) {
        Column {
            Row(
                modifier = Modifier
                    .padding(horizontal = Margin.normal)
                    .padding(top = Margin.normal)
            ) {
                Text(text = "Course: ${resource.course.courseCode}")
                Spacer(modifier = Modifier.weight(1f))
                Text(text = "Teacher: ${resource.instructor.initial}")
            }

            Text(
                modifier = Modifier
                    .padding(horizontal = Margin.normal)
                    .padding(bottom = Margin.normal, top = Margin.small),
                text = resource.name,
                fontWeight = FontWeight.W400,
                style = MaterialTheme.typography.h6,
                color = MaterialTheme.colors.secondary
            )

            Row(
                modifier = Modifier
                    .padding(horizontal = Margin.normal)
                    .padding(bottom = Margin.normal)
            ) {
                Text(text = "Uploaded by: ${resource.uploadedBy}")
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = "${resource.size} KB",
                    color = MaterialTheme.colors.error
                )
            }

            ProgressButton(
                type = progressType,
                onClick = onClick
            )
        }
    }
}

@Composable
@Preview(showBackground = true)
private fun PreviewResourceListItem() {
    SmartCRTheme {
        ScrollableColumn(modifier = Modifier.fillMaxWidth()) {
            (0..3).forEach {
                ResourceListItem(
                    modifier = Modifier.padding(Margin.normal),
                    resource = Resource(
                        name = "OOP Lecture 1 Presentation.pdf",
                        course = Course(courseCode = "CSE343"),
                        instructor = Instructor(initial = "NRC"),
                        uploadedBy = "Zoha"
                    ),
                    progressType = ProgressType.Download,
                    onClick = {}
                )
            }
        }
    }
}

sealed class ProgressType {
    abstract val title: String
    abstract val progress: Float

    data class Downloading(
        override val progress: Float,
        override val title: String = "Downloading..."
    ) : ProgressType()

    data class Uploading(
        override val progress: Float,
        override val title: String = "Uploading..."
    ) : ProgressType()

    data class View(val uri: Uri) : ProgressType() {
        override val title: String = "View"
        override val progress: Float = 1f
    }

    object Download : ProgressType() {
        override val title: String = "Download"
        override val progress: Float = 1f
    }
}

@Composable
private fun ProgressButton(
    type: ProgressType,
    onClick: () -> Unit,
) {
    val color = if (type is ProgressType.View) green500 else orange500

    val clickableModifier = if (type is ProgressType.View || type is ProgressType.Download)
        Modifier.clickable(onClick = onClick) else Modifier

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(40.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(color = color.lighten(0.5f))
        )

        Box(
            modifier = clickableModifier
                .fillMaxHeight()
                .fillMaxWidth(animate(type.progress))
                .background(color = color)
        )

        Text(
            modifier = Modifier.align(Alignment.Center),
            text = type.title,
            style = MaterialTheme.typography.button,
            color = MaterialTheme.colors.onPrimary
        )
    }
}

@Composable
@Preview(showBackground = true)
private fun PreviewProgressButton() {
    var progress by remember { mutableStateOf(0.1f) }
    val scope = rememberCoroutineScope()

    scope.launch {
        while (progress < 1f) {
            delay(2000)
            progress += 0.01f
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(Margin.normal)
    ) {
        ProgressButton(
            type = ProgressType.Downloading(progress),
            onClick = {}
        )

        ProgressButton(
            type = ProgressType.Uploading(progress),
            onClick = {}
        )

        ProgressButton(
            type = ProgressType.Download,
            onClick = {}
        )

        ProgressButton(
            type = ProgressType.View(Uri.EMPTY),
            onClick = {}
        )
    }
}