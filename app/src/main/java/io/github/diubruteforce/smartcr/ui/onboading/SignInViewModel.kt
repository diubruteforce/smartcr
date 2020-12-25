package io.github.diubruteforce.smartcr.ui.onboading

import androidx.hilt.lifecycle.ViewModelInject
import io.github.diubruteforce.smartcr.data.repository.ProfileRepository
import io.github.diubruteforce.smartcr.model.ui.EmptyLoadingState
import io.github.diubruteforce.smartcr.model.ui.Error
import io.github.diubruteforce.smartcr.model.ui.InputState
import io.github.diubruteforce.smartcr.model.ui.TypedSideEffectState
import io.github.diubruteforce.smartcr.utils.base.BaseViewModel

data class SignInState(
    val diuEmailState: InputState = InputState.DiuEmailState,
    val passwordState: InputState = InputState.PasswordState
)

enum class SignInSuccess {
    EMAIL_NOT_VERIFIED, NO_PROFILE_DATA, ALL_GOOD
}

class SignInViewModel @ViewModelInject constructor(
    private val profileRepository: ProfileRepository
) : BaseViewModel<SignInState, Any, SignInSuccess, String>(
    initialState = SignInState()
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
                profileRepository.signIn(
                    email = diuEmailState.value,
                    password = passwordState.value
                )

                val success = when {
                    profileRepository.isEmailVerified.not() -> {
                        SignInSuccess.EMAIL_NOT_VERIFIED
                    }
                    profileRepository.hasProfileData().not() -> {
                        SignInSuccess.NO_PROFILE_DATA
                    }
                    else -> {
                        SignInSuccess.ALL_GOOD
                    }
                }

                setSideEffect { TypedSideEffectState.Success(success) }
            }
        }
    }

    fun getUserEmail(): String = profileRepository.userEmail

    override fun onCoroutineException(exception: Throwable) {
        setSideEffect { TypedSideEffectState.Fail(exception.message ?: String.Error) }
    }
}