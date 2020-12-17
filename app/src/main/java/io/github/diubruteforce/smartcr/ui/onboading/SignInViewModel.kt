package io.github.diubruteforce.smartcr.ui.onboading

import androidx.hilt.lifecycle.ViewModelInject
import io.github.diubruteforce.smartcr.model.ui.SideEffect
import io.github.diubruteforce.smartcr.ui.common.TextFieldState
import io.github.diubruteforce.smartcr.utils.base.BaseViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import timber.log.Timber
import kotlin.random.Random

data class SignInState(
    val sideEffect: SideEffect = SideEffect.Uninitialized,
    val diuIdState: TextFieldState = TextFieldState.DiuEmailState,
    val passwordState: TextFieldState = TextFieldState.PasswordState
)

class SignInViewModel @ViewModelInject constructor() : BaseViewModel(){
    private val _state = MutableStateFlow(SignInState())
    val state: StateFlow<SignInState> get() = _state

    fun onDiuEmailChange(newDiuId: String){
        val newDiuIdState = _state.value.diuIdState.copy(value = newDiuId)
        _state.value = _state.value.copy(diuIdState = newDiuIdState)
    }

    fun onPasswordChange(newPassword: String){
        val newPasswordState = _state.value.passwordState.copy(value = newPassword)
        _state.value = _state.value.copy(passwordState = newPasswordState)
    }

    fun clearSideEffect(){
        _state.value = _state.value.copy(sideEffect = SideEffect.Uninitialized)
    }

    fun signIn(){
        val diuIdState = _state.value.diuIdState.validate()
        val passwordState = _state.value.passwordState.validate()

        val isError = diuIdState.isError || passwordState.isError

        if (isError){
            _state.value = _state.value.copy(diuIdState = diuIdState, passwordState = passwordState)
        } else {
            _state.value = _state.value.copy(
                sideEffect = SideEffect.Loading,
                diuIdState = diuIdState,
                passwordState = passwordState
            )

            launchInViewModelScope {
                delay(2000)

                if (Random.nextInt() % 2 == 1){
                    _state.value = _state.value.copy(sideEffect = SideEffect.Success)
                } else{
                    _state.value = _state.value.copy(sideEffect = SideEffect.Fail)
                }
            }
        }
    }
}