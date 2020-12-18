package io.github.diubruteforce.smartcr.ui.onboading

import io.github.diubruteforce.smartcr.model.ui.EmptyLoadingState
import io.github.diubruteforce.smartcr.model.ui.EmptySuccessState
import io.github.diubruteforce.smartcr.model.ui.StringFailSideEffectState
import io.github.diubruteforce.smartcr.model.ui.TypedSideEffectState
import io.github.diubruteforce.smartcr.ui.common.TextFieldState
import io.github.diubruteforce.smartcr.utils.base.BaseViewModel
import kotlinx.coroutines.delay
import kotlin.random.Random

data class SignUpState(
    val diuEmailState: TextFieldState = TextFieldState.DiuEmailState,
    val passwordState: TextFieldState = TextFieldState.PasswordState,
    val rePasswordState: TextFieldState = TextFieldState.RePasswordState,
)

class SignUpViewModel : BaseViewModel<SignUpState, StringFailSideEffectState>(
    initialState = SignUpState(),
    initialSideEffect = TypedSideEffectState.Uninitialized
) {
    fun onDiuEmailChange(newDiuId: String) = withState {
        val newDiuEmailState = diuEmailState.copy(value = newDiuId)
        setState { copy(diuEmailState = newDiuEmailState) }
    }

    fun onPasswordChange(newPassword: String) = withState {
        val newPasswordState = passwordState.copy(value = newPassword)
        setState { copy(passwordState = newPasswordState) }
    }

    fun onRePasswordChange(newRePassword: String) = withState {
        val newRePasswordState = rePasswordState.copy(value = newRePassword)
        setState { copy(rePasswordState = newRePasswordState) }
    }

    fun clearSideEffect() = setSideEffect { TypedSideEffectState.Uninitialized }

    fun signUp() = withState {
        val diuIdState = diuEmailState.validate()
        val passwordState = passwordState.validate()
        val rePasswordState = rePasswordState.copy(
            isError = passwordState.value != rePasswordState.value
        )

        val isError = diuIdState.isError ||
                passwordState.isError ||
                rePasswordState.isError

        setState {
            copy(
                diuEmailState = diuIdState,
                passwordState = passwordState,
                rePasswordState = rePasswordState
            )
        }

        if (isError.not()) {
            setSideEffect { EmptyLoadingState }

            launchInViewModelScope {
                delay(2000)

                if (Random.nextInt() % 2 == 1) {
                    setSideEffect { EmptySuccessState }
                } else {
                    setSideEffect { TypedSideEffectState.Fail("Something went wrong") }
                }
            }
        }
    }
}