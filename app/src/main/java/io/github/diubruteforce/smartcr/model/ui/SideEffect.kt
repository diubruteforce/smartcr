package io.github.diubruteforce.smartcr.model.ui

sealed class TypedSideEffect<out Loading, out Success, out Fail>{
    object Uninitialized: TypedSideEffect<Nothing, Nothing, Nothing>()
    class Loading<Loading>(val type: Loading): TypedSideEffect<Loading, Nothing, Nothing>()
    class Success<Success>(val type: Success): TypedSideEffect<Nothing, Success, Nothing>()
    class Fail<Fail>(val type: Fail): TypedSideEffect<Nothing, Nothing, Fail>()
}


sealed class SideEffect{
    object Uninitialized: SideEffect()
    object Loading: SideEffect()
    object Success: SideEffect()
    object Fail: SideEffect()
}