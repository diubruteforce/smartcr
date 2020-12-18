package io.github.diubruteforce.smartcr.ui.onboading

import io.github.diubruteforce.smartcr.model.ui.EmptyLoadingState
import io.github.diubruteforce.smartcr.model.ui.EmptySuccessState
import io.github.diubruteforce.smartcr.model.ui.StringFailSideEffectState
import io.github.diubruteforce.smartcr.model.ui.TypedSideEffectState
import io.github.diubruteforce.smartcr.ui.common.TextFieldState
import io.github.diubruteforce.smartcr.utils.base.BaseViewModel
import kotlinx.coroutines.delay
import kotlin.random.Random

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
                delay(2000)

                if (Random.nextInt() % 2 == 1) {
                    setSideEffect { EmptySuccessState }
                } else {
                    setSideEffect { TypedSideEffectState.Fail("Network Disconnected") }
                }
            }
        }
    }
}