package io.github.diubruteforce.smartcr.ui.onboading

import io.github.diubruteforce.smartcr.model.ui.EmptyLoadingState
import io.github.diubruteforce.smartcr.model.ui.EmptySuccessState
import io.github.diubruteforce.smartcr.model.ui.StringFailSideEffectState
import io.github.diubruteforce.smartcr.model.ui.TypedSideEffectState
import io.github.diubruteforce.smartcr.utils.base.BaseViewModel
import kotlinx.coroutines.delay
import kotlin.random.Random

object VerificationState

class VerificationViewModel : BaseViewModel<VerificationState, StringFailSideEffectState>(
    initialState = VerificationState,
    initialSideEffect = TypedSideEffectState.Uninitialized
) {
    fun checkVerificationStatus() {
        setSideEffect { EmptyLoadingState }

        launchInViewModelScope {
            delay(1000)

            if (Random.nextInt() % 2 == 1) {
                setSideEffect { TypedSideEffectState.Fail("Network Error") }
            } else {
                setSideEffect { EmptySuccessState }
            }
        }
    }

    fun sendVerificationEmail() {

    }
}