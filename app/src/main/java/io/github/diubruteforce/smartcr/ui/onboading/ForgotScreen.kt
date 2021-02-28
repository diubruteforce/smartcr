package io.github.diubruteforce.smartcr.ui.onboading

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import dev.chrisbanes.accompanist.insets.LocalWindowInsets
import dev.chrisbanes.accompanist.insets.navigationBarsWithImePadding
import io.github.diubruteforce.smartcr.R
import io.github.diubruteforce.smartcr.model.ui.InputState
import io.github.diubruteforce.smartcr.ui.common.CRAlertDialog
import io.github.diubruteforce.smartcr.ui.common.DiuEmail
import io.github.diubruteforce.smartcr.ui.common.LargeButton
import io.github.diubruteforce.smartcr.ui.common.SideEffect
import io.github.diubruteforce.smartcr.ui.theme.Margin
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.StateFlow

@OptIn(ExperimentalCoroutinesApi::class)
@Composable
fun ForgotScreen(
    viewModel: ForgotViewModel,
    navigateToSignIn: () -> Unit
) {
    val sideEffectState = viewModel.sideEffect.collectAsState().value
    var isEmailSent by remember { mutableStateOf(false) }

    SideEffect(
        sideEffectState = sideEffectState,
        onSuccess = { isEmailSent = true },
        onFailAlertDismissRequest = viewModel::clearSideEffect
    )

    if (isEmailSent) {
        CRAlertDialog(
            title = stringResource(id = R.string.email_sent),
            message = stringResource(id = R.string.email_sent_message),
            onDenial = { isEmailSent = false },
            denialText = stringResource(id = R.string.cancel),
            onAffirmation = navigateToSignIn,
            affirmationText = stringResource(id = R.string.sign_in),
            onDismissRequest = { isEmailSent = false }
        )
    }

    ForgotScreenContent(
        stateFlow = viewModel.state,
        onEmailChange = viewModel::onEmailChange,
        requestResetPassword = viewModel::requestPasswordReset
    )
}

@OptIn(ExperimentalCoroutinesApi::class)
@Composable
private fun ForgotScreenContent(
    stateFlow: StateFlow<InputState>,
    onEmailChange: (String) -> Unit,
    requestResetPassword: () -> Unit
) {
    Column(
        modifier = Modifier
            .background(Color.White)
            .fillMaxSize()
            .padding(horizontal = Margin.big)
            .navigationBarsWithImePadding(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val ime = LocalWindowInsets.current.ime
        val focusManager = LocalFocusManager.current
        val focusRequester = FocusRequester()

        val emailState = stateFlow.collectAsState().value

        Spacer(modifier = Modifier.weight(1f))

        Text(
            text = stringResource(id = R.string.forgot_password),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.h4
        )

        if (ime.isVisible.not()) {
            Image(
                modifier = Modifier
                    .fillMaxSize(0.35f)
                    .aspectRatio(1f),
                painter = painterResource(id = R.drawable.forgot_password),
                contentDescription = "Forgot Password"
            )
        } else {
            Spacer(modifier = Modifier.weight(1f))
        }

        DiuEmail(
            state = emailState,
            onValueChange = onEmailChange,
            focusRequester = focusRequester,
            imeAction = ImeAction.Done,
            onImeActionPerformed = {
                focusManager.clearFocus()
                requestResetPassword.invoke()
            }
        )

        Spacer(modifier = Modifier.weight(1f))

        LargeButton(
            text = stringResource(id = R.string.request_reset),
            onClick = requestResetPassword
        )

        Spacer(modifier = Modifier.height(Margin.big))
    }
}