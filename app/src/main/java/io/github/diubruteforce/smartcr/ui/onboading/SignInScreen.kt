package io.github.diubruteforce.smartcr.ui.onboading

import androidx.compose.foundation.layout.*
import androidx.compose.material.ButtonConstants
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.ExperimentalFocus
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.platform.AmbientFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import dev.chrisbanes.accompanist.insets.AmbientWindowInsets
import dev.chrisbanes.accompanist.insets.navigationBarsWithImePadding
import io.github.diubruteforce.smartcr.R
import io.github.diubruteforce.smartcr.ui.common.DiuId
import io.github.diubruteforce.smartcr.ui.common.LargeButton
import io.github.diubruteforce.smartcr.ui.common.Password
import io.github.diubruteforce.smartcr.ui.theme.Margin
import io.github.diubruteforce.smartcr.ui.theme.SmartCRTheme
import io.github.diubruteforce.smartcr.ui.theme.grayText
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

@Composable
fun SignInScreen(viewModel: SignInViewModel) {
    SignInScreenContent(
        stateFlow = viewModel.state,
        onDiuIdChange = viewModel::onDiuIdChange,
        onPasswordChange = viewModel::onPasswordChange,
        signIn = viewModel::signIn,
        navigateToSignUp = { /*TODO*/ },
        navigateToForgotPassword = { /*TODO*/ }
    )
}

@OptIn(ExperimentalFocus::class, ExperimentalCoroutinesApi::class)
@Composable
private fun SignInScreenContent(
    stateFlow: StateFlow<SignInState>,
    onDiuIdChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    signIn: () -> Unit,
    navigateToSignUp: () -> Unit,
    navigateToForgotPassword: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize()
            .padding(horizontal = Margin.big)
            .navigationBarsWithImePadding(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val ime = AmbientWindowInsets.current.ime
        val focusManager = AmbientFocusManager.current
        val diuIdFocusRequester = remember { FocusRequester() }
        val passwordFocusRequester = remember { FocusRequester() }
        val state = stateFlow.collectAsState().value

        Spacer(modifier = Modifier.weight(1f))

        Text(
            text = stringResource(id = R.string.welcome_student),
            style = MaterialTheme.typography.h4
        )

        Text(
            text = stringResource(id = R.string.stay_sign_in),
            style = MaterialTheme.typography.caption,
            color = MaterialTheme.colors.grayText
        )

        Spacer(modifier = Modifier.weight(1f))

        DiuId(
            state = state.diuIdState,
            onValueChange = onDiuIdChange,
            focusRequester = diuIdFocusRequester,
            onImeActionPerformed = { passwordFocusRequester.requestFocus() }
        )

        Spacer(modifier = Modifier.height(Margin.tiny))

        Password(
            state = state.passwordState,
            onValueChange = onPasswordChange,
            focusRequester = passwordFocusRequester,
            onImeActionPerformed = { focusManager.clearFocus(forcedClear = true) }
        )

        if (!ime.isVisible) {
            Spacer(modifier = Modifier.height(Margin.normal))

            TextButton(
                onClick = navigateToForgotPassword,
                colors = ButtonConstants
                    .defaultOutlinedButtonColors(contentColor = MaterialTheme.colors.grayText)
            ) {
                Text(
                    text = stringResource(id = R.string.forgot_password),
                    style = MaterialTheme.typography.body1
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        LargeButton(
            text = stringResource(id = R.string.sign_in),
            onClick = signIn
        )

        if (!ime.isVisible) {
            Spacer(modifier = Modifier.height(Margin.big))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = stringResource(id = R.string.dont_have_account),
                    color = MaterialTheme.colors.grayText
                )

                TextButton(
                    onClick = navigateToSignUp,
                    colors = ButtonConstants
                        .defaultOutlinedButtonColors(contentColor = MaterialTheme.colors.error)
                ) {
                    Text(
                        text = stringResource(id = R.string.sign_up),
                        style = MaterialTheme.typography.body1
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(Margin.big))
    }
}

@Composable
@Preview(showBackground = true)
private fun PreviewSignInScreenContent() {
    SmartCRTheme {
        SignInScreenContent(
            stateFlow = MutableStateFlow(SignInState()),
            onDiuIdChange = { /*TODO*/ },
            onPasswordChange = { /*TODO*/ },
            signIn = { /*TODO*/ },
            navigateToSignUp = { /*TODO*/ },
            navigateToForgotPassword = { /*TODO*/ }
        )
    }
}