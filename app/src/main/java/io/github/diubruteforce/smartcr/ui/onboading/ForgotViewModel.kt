package io.github.diubruteforce.smartcr.ui.onboading

import androidx.hilt.lifecycle.ViewModelInject
import io.github.diubruteforce.smartcr.data.repository.ProfileRepository
import io.github.diubruteforce.smartcr.model.ui.*
import io.github.diubruteforce.smartcr.utils.base.BaseViewModel

class ForgotViewModel @ViewModelInject constructor(
    private val profileRepository: ProfileRepository
) : BaseViewModel<InputState, StringFailSideEffectState>(
    initialState = InputState.DiuEmailState,
    initialSideEffect = TypedSideEffectState.Uninitialized
) {
    fun onEmailChange(newEmail: String) = withState {
        setState { copy(value = newEmail) }
    }

    fun requestPasswordReset() = withState {
        val newEmailState = validate()

        setState { newEmailState }

        if (newEmailState.isError.not()) {
            setSideEffect { EmptyLoadingState }

            launchInViewModelScope {
                try {
                    profileRepository.requestPasswordReset(newEmailState.value)
                    setSideEffect { EmptySuccessState }
                } catch (ex: Exception) {
                    setSideEffect { TypedSideEffectState.Fail(ex.message ?: String.Error) }
                }
            }
        }
    }
}