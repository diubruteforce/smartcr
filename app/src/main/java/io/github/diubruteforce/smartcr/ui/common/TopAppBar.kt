package io.github.diubruteforce.smartcr.ui.common

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.KeyboardArrowLeft
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.chrisbanes.accompanist.coil.CoilImage
import dev.chrisbanes.accompanist.insets.AmbientWindowInsets
import dev.chrisbanes.accompanist.insets.statusBarsPadding
import io.github.diubruteforce.smartcr.R
import io.github.diubruteforce.smartcr.ui.theme.Margin

@Composable
fun ProfileTopAppBar(
    title: String,
    navigationIcon: @Composable (() -> Unit)? = null,
    actions: @Composable RowScope.() -> Unit = {},
    imageUrl: String,
    imageCaption: @Composable () -> Unit
) {
    Card(
        shape = RoundedCornerShape(0.dp),
        elevation = 8.dp
    ) {
        ConstraintLayout(modifier = Modifier.fillMaxWidth()) {
            val ime = AmbientWindowInsets.current.ime
            val (bgRef, contentRef) = createRefs()

            if (ime.isVisible.not()) {
                Image(
                    modifier = Modifier
                        .constrainAs(bgRef) {
                            linkTo(start = parent.start, end = parent.end)
                            linkTo(top = parent.top, bottom = parent.bottom)

                            width = Dimension.fillToConstraints
                            height = Dimension.fillToConstraints
                        },
                    contentScale = ContentScale.Crop,
                    bitmap = imageResource(id = R.drawable.profile_bg)
                )
            }

            Column(
                modifier = Modifier
                    .constrainAs(contentRef) {
                        linkTo(start = parent.start, end = parent.end)
                        linkTo(top = parent.top, bottom = parent.bottom)

                        width = Dimension.fillToConstraints
                        height = Dimension.wrapContent
                    },
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                InsetAwareTopAppBar(
                    backgroundColor = if (ime.isVisible) MaterialTheme.colors.surface else Color.Transparent,
                    elevation = if (ime.isVisible) 4.dp else 0.dp
                ) {
                    navigationIcon?.invoke()

                    Spacer(modifier = Modifier.weight(1f))

                    Text(
                        modifier = Modifier.align(Alignment.CenterVertically),
                        text = title
                    )

                    Spacer(modifier = Modifier.weight(1f))

                    actions.invoke(this)
                }

                if (ime.isVisible.not()) {
                    Spacer(modifier = Modifier.size(Margin.medium))

                    Card(
                        modifier = Modifier.fillMaxWidth(0.3f).aspectRatio(1f),
                        backgroundColor = Color.LightGray,
                        shape = RoundedCornerShape(8.dp),
                        elevation = 8.dp
                    ) {
                        CoilImage(
                            data = imageUrl,
                            fadeIn = true,
                            contentScale = ContentScale.Crop
                        )
                    }

                    Spacer(modifier = Modifier.size(Margin.normal))

                    imageCaption.invoke()

                    Spacer(modifier = Modifier.size(Margin.normal))
                }
            }
        }
    }
}

@Composable
fun BackPressTopAppBar(
    modifier: Modifier = Modifier,
    onBackPress: () -> Unit,
    title: String
) {
    InsetAwareTopAppBar(modifier = modifier) {
        IconButton(
            modifier = Modifier.align(Alignment.CenterVertically),
            onClick = onBackPress
        ) {
            Icon(imageVector = Icons.Outlined.KeyboardArrowLeft)
        }

        Spacer(modifier = Modifier.weight(1f))

        Text(
            modifier = Modifier.align(Alignment.CenterVertically),
            text = title
        )

        Spacer(modifier = Modifier.weight(1f))

        IconButton(onClick = {}) {}
    }
}

@Composable
fun InsetAwareTopAppBar(
    modifier: Modifier = Modifier,
    backgroundColor: Color = MaterialTheme.colors.surface,
    contentColor: Color = contentColorFor(backgroundColor),
    elevation: Dp = 4.dp,
    content: @Composable RowScope.() -> Unit
) {
    Surface(
        color = backgroundColor,
        elevation = elevation,
        modifier = modifier
    ) {
        TopAppBar(
            modifier = Modifier.statusBarsPadding(),
            backgroundColor = Color.Transparent,
            contentColor = contentColor,
            elevation = 0.dp,
            content = content
        )
    }
}

@Composable
@Preview(showBackground = true)
private fun PreviewProfileTopAbbBar() {

}