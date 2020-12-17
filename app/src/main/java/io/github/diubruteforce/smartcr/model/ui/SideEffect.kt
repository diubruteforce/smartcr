package io.github.diubruteforce.smartcr.model.ui

sealed class TypedSideEffect<out Loading, out Success, out Fail> {
    object Uninitialized : TypedSideEffect<Nothing, Nothing, Nothing>()
    class Loading<Loading>(val type: Loading) : TypedSideEffect<Loading, Nothing, Nothing>()
    class Success<Success>(val type: Success) : TypedSideEffect<Nothing, Success, Nothing>()
    class Fail<Fail>(val type: Fail) : TypedSideEffect<Nothing, Nothing, Fail>()
}

typealias EmptySideEffect = TypedSideEffect<Any, Any, Any>

val EmptyLoading = TypedSideEffect.Loading(1)
val EmptySuccess = TypedSideEffect.Success(2)
val EmptyFail = TypedSideEffect.Fail(3)

typealias StringFailSideEffect = TypedSideEffect<Any, Any, String>