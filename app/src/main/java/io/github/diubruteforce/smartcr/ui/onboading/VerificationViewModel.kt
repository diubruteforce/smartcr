package io.github.diubruteforce.smartcr.ui.onboading

import androidx.hilt.lifecycle.ViewModelInject
import io.github.diubruteforce.smartcr.data.repository.ProfileRepository
import io.github.diubruteforce.smartcr.model.ui.EmptyLoadingState
import io.github.diubruteforce.smartcr.model.ui.TypedSideEffectState
import io.github.diubruteforce.smartcr.utils.base.StringFailSideEffectViewModel
import timber.log.Timber

object VerificationState

class VerificationViewModel @ViewModelInject constructor(
    private val profileRepository: ProfileRepository
) : StringFailSideEffectViewModel<VerificationState>(
    initialState = VerificationState
) {
    fun checkVerificationStatus() {
        setSideEffect { EmptyLoadingState }

        launchInViewModelScope {
            profileRepository.signOut()
            setSideEffect { TypedSideEffectState.Fail("To get started sign in.") }
        }
    }

    fun sendVerificationEmail() {
        setSideEffect { EmptyLoadingState }

        launchInViewModelScope {
            try {
                profileRepository.sendVerificationEmail()
            } catch (ex: Exception) {
                Timber.e(ex)
            } finally {
                setSideEffect { TypedSideEffectState.Uninitialized }
            }
        }
    }
}