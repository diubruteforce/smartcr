package io.github.diubruteforce.smartcr.ui.onboading

import io.github.diubruteforce.smartcr.data.repository.AuthRepository
import io.github.diubruteforce.smartcr.model.ui.EmptyLoadingState
import io.github.diubruteforce.smartcr.model.ui.InputState
import io.github.diubruteforce.smartcr.model.ui.TypedSideEffectState
import io.github.diubruteforce.smartcr.utils.base.BaseViewModel

data class SignUpState(
    val diuEmailState: InputState = InputState.DiuEmailState,
    val passwordState: InputState = InputState.PasswordState,
    val rePasswordState: InputState = InputState.RePasswordState,
)

class SignUpViewModel : BaseViewModel<SignUpState, TypedSideEffectState<Any, String, String>>(
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

    fun signUp() = withState {
        val diuEmailState = diuEmailState.validate()
        val passwordState = passwordState.validate()
        val rePasswordState = rePasswordState.copy(
            isError = passwordState.value != rePasswordState.value
        )

        val isError = diuEmailState.isError ||
                passwordState.isError ||
                rePasswordState.isError

        setState {
            copy(
                diuEmailState = diuEmailState,
                passwordState = passwordState,
                rePasswordState = rePasswordState
            )
        }

        if (isError.not()) {
            setSideEffect { EmptyLoadingState }

            launchInViewModelScope {
                try {
                    AuthRepository.createNewUser(
                        email = diuEmailState.value,
                        password = passwordState.value
                    )

                    setSideEffect { TypedSideEffectState.Success(diuEmailState.value) }
                } catch (ex: Exception) {
                    setSideEffect {
                        TypedSideEffectState.Fail(
                            ex.message ?: "Something went wrong"
                        )
                    }
                }
            }
        }
    }
}