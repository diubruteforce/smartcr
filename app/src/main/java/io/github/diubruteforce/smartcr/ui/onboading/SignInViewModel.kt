package io.github.diubruteforce.smartcr.ui.onboading

import io.github.diubruteforce.smartcr.data.repository.AuthRepository
import io.github.diubruteforce.smartcr.model.ui.*
import io.github.diubruteforce.smartcr.utils.base.BaseViewModel
import timber.log.Timber

data class SignInState(
    val diuEmailState: InputState = InputState.DiuEmailState,
    val passwordState: InputState = InputState.PasswordState
)

class SignInViewModel : BaseViewModel<SignInState, StringFailSideEffectState>(
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
        val diuEmailState = diuEmailState.validate()
        val passwordState = passwordState.validate()

        val isError = diuEmailState.isError || passwordState.isError

        setState { copy(diuEmailState = diuEmailState, passwordState = passwordState) }

        if (isError.not()) {
            setSideEffect { EmptyLoadingState }

            launchInViewModelScope {
                try {
                    AuthRepository.signIn(
                        email = diuEmailState.value,
                        password = passwordState.value
                    )

                    setSideEffect { EmptySuccessState }
                } catch (ex: Exception) {
                    Timber.e(ex)
                    setSideEffect {
                        TypedSideEffectState.Fail(ex.message ?: "Something went wrong")
                    }
                }
            }
        }
    }

    fun isEmailVerified() = AuthRepository.isEmailVerified

    fun getUserEmail(): String = AuthRepository.userEmail

    fun hasProfileData(): Boolean = false
}