package io.github.diubruteforce.smartcr.ui.onboading

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import dev.chrisbanes.accompanist.insets.navigationBarsWithImePadding
import io.github.diubruteforce.smartcr.R
import io.github.diubruteforce.smartcr.ui.common.LargeButton
import io.github.diubruteforce.smartcr.ui.common.SideEffect
import io.github.diubruteforce.smartcr.ui.theme.Margin
import io.github.diubruteforce.smartcr.ui.theme.grayText
import kotlinx.coroutines.ExperimentalCoroutinesApi

@OptIn(ExperimentalCoroutinesApi::class)
@Composable
fun VerificationScreen(
    email: String,
    viewModel: VerificationViewModel,
    navigateToSignIn: () -> Unit
) {
    val sideEffectState = viewModel.sideEffect.collectAsState().value

    SideEffect(
        sideEffectState = sideEffectState,
        onSuccess = { },
        onFailAlertDismissRequest = viewModel::clearSideEffect,
        title = stringResource(id = R.string.awesome),
        affirmationText = stringResource(id = R.string.sign_in),
        onFailAlertAffirmation = {
            viewModel.sendVerificationEmail()
            viewModel.clearSideEffect()
            navigateToSignIn.invoke()
        }
    )

    VerificationScreenContent(
        email = email,
        checkVerificationStatus = viewModel::checkVerificationStatus,
        sendVerificationEmail = viewModel::sendVerificationEmail
    )
}

@Composable
private fun VerificationScreenContent(
    email: String,
    checkVerificationStatus: () -> Unit,
    sendVerificationEmail: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize()
            .padding(horizontal = Margin.big)
            .navigationBarsWithImePadding(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Spacer(modifier = Modifier.weight(1f))

        Text(
            text = stringResource(id = R.string.check_your_email_for_verification),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.h4
        )

        Image(
            modifier = Modifier.fillMaxSize(0.35f).aspectRatio(1f),
            imageVector = vectorResource(id = R.drawable.email)
        )

        Text(
            text = stringResource(id = R.string.we_have_sent_you_an_email, email),
            textAlign = TextAlign.Center,
            color = MaterialTheme.colors.grayText
        )

        Spacer(modifier = Modifier.weight(1f))

        LargeButton(
            text = stringResource(id = R.string.i_have_verified),
            onClick = checkVerificationStatus
        )

        Spacer(modifier = Modifier.height(Margin.big))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = stringResource(id = R.string.didnt_recieve_any_email),
                color = MaterialTheme.colors.grayText
            )

            TextButton(
                onClick = sendVerificationEmail,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = MaterialTheme.colors.error
                )
            ) {
                Text(
                    text = stringResource(id = R.string.send_again),
                    style = MaterialTheme.typography.body1
                )
            }
        }

        Spacer(modifier = Modifier.height(Margin.big))
    }
}