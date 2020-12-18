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
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.platform.AmbientFocusManager
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import dev.chrisbanes.accompanist.insets.AmbientWindowInsets
import dev.chrisbanes.accompanist.insets.navigationBarsWithImePadding
import dev.chrisbanes.accompanist.insets.statusBarsPadding
import io.github.diubruteforce.smartcr.R
import io.github.diubruteforce.smartcr.ui.common.DiuEmail
import io.github.diubruteforce.smartcr.ui.common.LargeButton
import io.github.diubruteforce.smartcr.ui.common.Password
import io.github.diubruteforce.smartcr.ui.common.SideEffect
import io.github.diubruteforce.smartcr.ui.theme.Margin
import io.github.diubruteforce.smartcr.ui.theme.grayText
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.StateFlow


@OptIn(ExperimentalCoroutinesApi::class)
@Composable
fun SignUpScreen(
    viewModel: SignUpViewModel,
    navigateToSignIn: () -> Unit,
    navigateToEmailVerification: (String) -> Unit
) {
    val sideEffect = viewModel.sideEffect.collectAsState().value

    SideEffect(
        sideEffectState = sideEffect,
        onSuccess = { navigateToEmailVerification.invoke(it) },
        onFailAlertDismissRequest = viewModel::clearSideEffect
    )

    SignUpScreenContent(
        stateFlow = viewModel.state,
        navigateToSignIn = navigateToSignIn,
        signUp = viewModel::signUp,
        onDiuEmailChange = viewModel::onDiuEmailChange,
        onPasswordChange = viewModel::onPasswordChange,
        onRePasswordChange = viewModel::onRePasswordChange
    )
}

@OptIn(ExperimentalCoroutinesApi::class)
@Composable
private fun SignUpScreenContent(
    stateFlow: StateFlow<SignUpState>,
    navigateToSignIn: () -> Unit,
    signUp: () -> Unit,
    onDiuEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onRePasswordChange: (String) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize()
            .padding(horizontal = Margin.big)
            .statusBarsPadding()
            .navigationBarsWithImePadding(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val ime = AmbientWindowInsets.current.ime
        val focusManager = AmbientFocusManager.current
        val (diuEmailFocusRequester,
            passwordFocusRequester,
            rePasswordFocusRequester) = FocusRequester.createRefs()
        val state = stateFlow.collectAsState().value

        if (ime.isVisible.not()) {
            Image(
                modifier = Modifier.fillMaxWidth(0.5f).aspectRatio(1f),
                bitmap = imageResource(id = R.drawable.sign_up)
            )
        }

        Spacer(modifier = Modifier.height(Margin.medium))

        Text(
            text = stringResource(id = R.string.fill_up_the_details_to_get_started),
            style = MaterialTheme.typography.body1,
            color = MaterialTheme.colors.grayText
        )

        Spacer(modifier = Modifier.height(Margin.big))

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
            imeAction = ImeAction.Next,
            onImeActionPerformed = { rePasswordFocusRequester.requestFocus() }
        )

        Spacer(modifier = Modifier.height(Margin.tiny))

        Password(
            state = state.rePasswordState,
            onValueChange = onRePasswordChange,
            placeHolder = stringResource(id = R.string.re_enter_your_password),
            focusRequester = rePasswordFocusRequester,
            onImeActionPerformed = {
                focusManager.clearFocus(forcedClear = true)
                signUp.invoke()
            }
        )

        Spacer(modifier = Modifier.weight(1f))

        LargeButton(
            text = stringResource(id = R.string.next),
            onClick = signUp
        )

        if (!ime.isVisible) {
            Spacer(modifier = Modifier.height(Margin.big))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = stringResource(id = R.string.already_have_an_account),
                    color = MaterialTheme.colors.grayText
                )

                TextButton(
                    onClick = navigateToSignIn,
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colors.error
                    )
                ) {
                    Text(
                        text = stringResource(id = R.string.sign_in),
                        style = MaterialTheme.typography.body1
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(Margin.big))
    }
}
