package io.github.diubruteforce.smartcr.ui.onboading

import io.github.diubruteforce.smartcr.data.repository.AuthRepository
import io.github.diubruteforce.smartcr.model.ui.*
import io.github.diubruteforce.smartcr.ui.common.TextFieldState
import io.github.diubruteforce.smartcr.utils.base.BaseViewModel

class ForgotViewModel : BaseViewModel<TextFieldState, StringFailSideEffectState>(
    initialState = TextFieldState.DiuEmailState,
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
                    AuthRepository.requestPasswordReset(newEmailState.value)
                    setSideEffect { EmptySuccessState }
                } catch (ex: Exception) {
                    setSideEffect { TypedSideEffectState.Fail(ex.message ?: GeneralError) }
                }
            }
        }
    }
}