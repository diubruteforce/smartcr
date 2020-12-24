package io.github.diubruteforce.smartcr.ui.onboading

import androidx.hilt.lifecycle.ViewModelInject
import io.github.diubruteforce.smartcr.data.repository.ProfileRepository
import io.github.diubruteforce.smartcr.utils.base.StringFailSideEffectViewModel

class SplashViewModel @ViewModelInject constructor(
    private val profileRepository: ProfileRepository
) : StringFailSideEffectViewModel<Boolean>(
    initialState = false
) {
    fun isAuthenticated() = profileRepository.isAuthenticated

    fun isEmailVerified() = profileRepository.isEmailVerified

    fun getUserEmail(): String = profileRepository.userEmail

    suspend fun hasProfileData(): Boolean = profileRepository.hasProfileData()
}