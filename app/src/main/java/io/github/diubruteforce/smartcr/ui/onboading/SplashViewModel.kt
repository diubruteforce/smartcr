package io.github.diubruteforce.smartcr.ui.onboading

import io.github.diubruteforce.smartcr.data.repository.AuthRepository
import io.github.diubruteforce.smartcr.model.ui.StringFailSideEffectState
import io.github.diubruteforce.smartcr.model.ui.TypedSideEffectState
import io.github.diubruteforce.smartcr.utils.base.BaseViewModel

class SplashViewModel : BaseViewModel<Boolean, StringFailSideEffectState>(
    initialState = false,
    initialSideEffect = TypedSideEffectState.Uninitialized
) {
    fun isAuthenticated() = AuthRepository.isAuthenticated

    fun isEmailVerified() = AuthRepository.isEmailVerified

    fun getUserEmail(): String = AuthRepository.userEmail

    fun hasProfileData(): Boolean = false
}