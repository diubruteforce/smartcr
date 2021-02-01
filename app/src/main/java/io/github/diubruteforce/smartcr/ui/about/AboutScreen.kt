package io.github.diubruteforce.smartcr.ui.about

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import dev.chrisbanes.accompanist.insets.navigationBarsPadding
import io.github.diubruteforce.smartcr.BuildConfig
import io.github.diubruteforce.smartcr.R
import io.github.diubruteforce.smartcr.ui.common.BackPressTopAppBar
import io.github.diubruteforce.smartcr.ui.theme.Margin
import io.github.diubruteforce.smartcr.ui.theme.grayText

@Composable
fun AboutScreen(
    onBackPress: () -> Unit
) {
    Scaffold(topBar = {
        BackPressTopAppBar(onBackPress = onBackPress, title = "About App")
    }) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .navigationBarsPadding()
        ) {

            Spacer(modifier = Modifier.size(Margin.large))

            Image(
                modifier = Modifier
                    .size(100.dp)
                    .align(Alignment.CenterHorizontally),
                imageVector = vectorResource(id = R.drawable.logo_primary),
                contentDescription = "SmartCR"
            )

            Spacer(modifier = Modifier.size(Margin.medium))

            Text(
                modifier = Modifier.padding(horizontal = Margin.large),
                text = stringResource(id = R.string.about_app),
                textAlign = TextAlign.Justify
            )

            Spacer(modifier = Modifier.weight(1f))

            Text(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                text = "SmartCR v${BuildConfig.FOOD_VERSION}",
                color = MaterialTheme.colors.grayText
            )

            Spacer(modifier = Modifier.size(Margin.medium))
        }
    }
}