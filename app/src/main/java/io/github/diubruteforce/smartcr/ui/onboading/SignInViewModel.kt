package io.github.diubruteforce.smartcr.ui.onboading

import io.github.diubruteforce.smartcr.model.ui.*
import io.github.diubruteforce.smartcr.ui.common.TextFieldState
import io.github.diubruteforce.smartcr.utils.base.BaseViewModel
import kotlinx.coroutines.delay
import kotlin.random.Random

data class SignInState(
    val diuEmailState: TextFieldState = TextFieldState.DiuEmailState,
    val passwordState: TextFieldState = TextFieldState.PasswordState
)

class SignInViewModel : BaseViewModel<SignInState, EmptySideEffectState>(
    initialState = SignInState(),
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

    fun signIn() = withState {
        val diuIdState = diuEmailState.validate()
        val passwordState = passwordState.validate()

        val isError = diuIdState.isError || passwordState.isError

        setState { copy(diuEmailState = diuIdState, passwordState = passwordState) }

        if (isError.not()) {
            setSideEffect { EmptyLoadingState }

            launchInViewModelScope {
                delay(2000)

                if (Random.nextInt() % 2 == 1) {
                    setSideEffect { EmptySuccessState }
                } else {
                    setSideEffect { EmptyFailState }
                }
            }
        }
    }
}