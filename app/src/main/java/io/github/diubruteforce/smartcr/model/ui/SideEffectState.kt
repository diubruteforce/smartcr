package io.github.diubruteforce.smartcr.model.ui

sealed class TypedSideEffectState<out Loading, out Success, out Fail> {
    object Uninitialized : TypedSideEffectState<Nothing, Nothing, Nothing>()
    class Loading<Loading>(val type: Loading) : TypedSideEffectState<Loading, Nothing, Nothing>()
    class Success<Success>(val type: Success) : TypedSideEffectState<Nothing, Success, Nothing>()
    class Fail<Fail>(val type: Fail) : TypedSideEffectState<Nothing, Nothing, Fail>()
}

typealias EmptySideEffectState = TypedSideEffectState<Any, Any, Any>

val EmptyLoadingState = TypedSideEffectState.Loading(1)
val EmptySuccessState = TypedSideEffectState.Success(2)
val EmptyFailState = TypedSideEffectState.Fail(3)

typealias StringFailSideEffectState = TypedSideEffectState<Any, Any, String>