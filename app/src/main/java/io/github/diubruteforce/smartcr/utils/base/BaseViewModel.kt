package io.github.diubruteforce.smartcr.utils.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

abstract class BaseViewModel<State, SideEffect>(
    initialState: State,
    private val initialSideEffect: SideEffect
) : ViewModel() {
    private val _state = MutableStateFlow(initialState)
    val state: StateFlow<State> get() = _state

    private val _sideEffect = MutableStateFlow(initialSideEffect)
    val sideEffect: StateFlow<SideEffect> get() = _sideEffect

    protected fun withState(block: State.() -> Unit) = block.invoke(_state.value)

    protected fun setState(block: () -> State) {
        _state.value = block.invoke()
    }

    protected fun withSideEffect(block: SideEffect.() -> Unit) = block.invoke(_sideEffect.value)

    protected fun setSideEffect(block: () -> SideEffect) {
        _sideEffect.value = block.invoke()
    }

    fun clearSideEffect() = setSideEffect { initialSideEffect }

    // region: This will handle the unHandled exception inside coroutine
    private val handler = CoroutineExceptionHandler { _, ex -> Timber.e(ex) }

    protected fun launchInViewModelScope(
        context: CoroutineContext = EmptyCoroutineContext,
        start: CoroutineStart = CoroutineStart.DEFAULT,
        block: suspend CoroutineScope.() -> Unit
    ) = viewModelScope.launch(context = context + handler, start = start, block = block)
    // endregion
}