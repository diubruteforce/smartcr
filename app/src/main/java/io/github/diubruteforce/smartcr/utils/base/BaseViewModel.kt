package io.github.diubruteforce.smartcr.utils.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.diubruteforce.smartcr.model.ui.Error
import io.github.diubruteforce.smartcr.model.ui.TypedSideEffectState
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

abstract class BaseViewModel<State, Loading, Success, Fail>(
    initialState: State
) : ViewModel() {
    private val _state = MutableStateFlow(initialState)
    val state: StateFlow<State> get() = _state

    private val _sideEffect: MutableStateFlow<TypedSideEffectState<Loading, Success, Fail>> =
        MutableStateFlow(TypedSideEffectState.Uninitialized)
    val sideEffect: StateFlow<TypedSideEffectState<Loading, Success, Fail>> get() = _sideEffect

    protected fun withState(block: State.() -> Unit) = block.invoke(_state.value)

    protected fun setState(block: () -> State) {
        _state.value = block.invoke()
    }

    protected fun withSideEffect(block: TypedSideEffectState<Loading, Success, Fail>.() -> Unit) =
        block.invoke(_sideEffect.value)

    protected fun setSideEffect(block: () -> TypedSideEffectState<Loading, Success, Fail>) {
        _sideEffect.value = block.invoke()
    }

    fun clearSideEffect() = setSideEffect { TypedSideEffectState.Uninitialized }

    // region: This will handle the unHandled exception inside coroutine
    private val handler = CoroutineExceptionHandler { _, ex ->
        Timber.e(ex)
        onCoroutineException(ex)
    }

    abstract fun onCoroutineException(exception: Throwable)

    protected fun launchInViewModelScope(
        context: CoroutineContext = EmptyCoroutineContext,
        start: CoroutineStart = CoroutineStart.DEFAULT,
        block: suspend CoroutineScope.() -> Unit
    ) = viewModelScope.launch(context = context + handler, start = start, block = block)
    // endregion
}

abstract class StringFailSideEffectViewModel<State>(
    initialState: State
) : BaseViewModel<State, Any, Any, String>(
    initialState = initialState,
) {
    override fun onCoroutineException(exception: Throwable) {
        setSideEffect { TypedSideEffectState.Fail(exception.message ?: String.Error) }
    }
}