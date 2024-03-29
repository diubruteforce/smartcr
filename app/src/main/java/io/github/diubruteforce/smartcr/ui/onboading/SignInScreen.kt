package io.github.diubruteforce.smartcr.ui.onboading

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import dev.chrisbanes.accompanist.insets.LocalWindowInsets
import dev.chrisbanes.accompanist.insets.navigationBarsWithImePadding
import io.github.diubruteforce.smartcr.R
import io.github.diubruteforce.smartcr.ui.common.DiuEmail
import io.github.diubruteforce.smartcr.ui.common.LargeButton
import io.github.diubruteforce.smartcr.ui.common.Password
import io.github.diubruteforce.smartcr.ui.common.SideEffect
import io.github.diubruteforce.smartcr.ui.theme.Margin
import io.github.diubruteforce.smartcr.ui.theme.SmartCRTheme
import io.github.diubruteforce.smartcr.ui.theme.grayText
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

@OptIn(ExperimentalCoroutinesApi::class)
@Composable
fun SignInScreen(
    viewModel: SignInViewModel,
    navigateToHome: () -> Unit,
    navigateToProfileEdit: () -> Unit,
    navigateToVerification: (String) -> Unit,
    navigateToSignUp: () -> Unit,
    navigateToForgotPassword: () -> Unit,
) {
    val sideEffect = viewModel.sideEffect.collectAsState().value

    SideEffect(
        sideEffectState = sideEffect,
        onSuccess = {
            when (it) {
                SignInSuccess.EMAIL_NOT_VERIFIED ->
                    navigateToVerification.invoke(viewModel.getUserEmail())
                SignInSuccess.NO_PROFILE_DATA -> navigateToProfileEdit.invoke()
                SignInSuccess.ALL_GOOD -> navigateToHome.invoke()
            }
        },
        onFailAlertDismissRequest = viewModel::clearSideEffect,
        denialText = stringResource(id = R.string.reset),
        affirmationText = stringResource(id = R.string.try_again)
    )

    SignInScreenContent(
        stateFlow = viewModel.state,
        onDiuEmailChange = viewModel::onDiuEmailChange,
        onPasswordChange = viewModel::onPasswordChange,
        signIn = viewModel::signIn,
        navigateToSignUp = navigateToSignUp,
        navigateToForgotPassword = navigateToForgotPassword
    )
}

@OptIn(ExperimentalCoroutinesApi::class)
@Composable
private fun SignInScreenContent(
    stateFlow: StateFlow<SignInState>,
    onDiuEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    signIn: () -> Unit,
    navigateToSignUp: () -> Unit,
    navigateToForgotPassword: () -> Unit
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
        val diuEmailFocusRequester = remember { FocusRequester() }
        val passwordFocusRequester = remember { FocusRequester() }
        val state = stateFlow.collectAsState().value

        Spacer(modifier = Modifier.weight(1f))

        Text(
            text = stringResource(id = R.string.welcome_student),
            style = MaterialTheme.typography.h4
        )

        Text(
            text = stringResource(id = R.string.stay_sign_in),
            style = MaterialTheme.typography.body2,
            color = MaterialTheme.colors.grayText
        )

        Spacer(modifier = Modifier.weight(1f))

        DiuEmail(
            state = state.diuEmailState,
            onValueChange = onDiuEmailChange,
            focusRequester = diuEmailFocusRequester,
            onImeActionPerformed = { passwordFocusRequester.requestFocus() }
        )

        Spacer(modifier = Modifier.height(Margin.tiny))

        Password(
            state = state.passwordState,
            onValueChange = onPasswordChange,
            focusRequester = passwordFocusRequester,
            onImeActionPerformed = {
                focusManager.clearFocus(forcedClear = true)
                signIn.invoke()
            }
        )

        if (!ime.isVisible) {
            Spacer(modifier = Modifier.height(Margin.normal))

            TextButton(
                onClick = navigateToForgotPassword,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = MaterialTheme.colors.grayText
                )
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
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colors.error
                    )
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
            onDiuEmailChange = { },
            onPasswordChange = { },
            signIn = { },
            navigateToSignUp = { },
            navigateToForgotPassword = { }
        )
    }
}