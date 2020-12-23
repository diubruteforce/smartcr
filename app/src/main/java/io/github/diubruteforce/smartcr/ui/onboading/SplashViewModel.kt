package io.github.diubruteforce.smartcr.ui.onboading

import androidx.hilt.lifecycle.ViewModelInject
import io.github.diubruteforce.smartcr.data.repository.ProfileRepository
import io.github.diubruteforce.smartcr.model.ui.StringFailSideEffectState
import io.github.diubruteforce.smartcr.model.ui.TypedSideEffectState
import io.github.diubruteforce.smartcr.utils.base.BaseViewModel

class SplashViewModel @ViewModelInject constructor(
    private val profileRepository: ProfileRepository
) : BaseViewModel<Boolean, StringFailSideEffectState>(
    initialState = false,
    initialSideEffect = TypedSideEffectState.Uninitialized
) {
    fun isAuthenticated() = profileRepository.isAuthenticated

    fun isEmailVerified() = profileRepository.isEmailVerified

    fun getUserEmail(): String = profileRepository.userEmail

    suspend fun hasProfileData(): Boolean = profileRepository.hasProfileData()
}