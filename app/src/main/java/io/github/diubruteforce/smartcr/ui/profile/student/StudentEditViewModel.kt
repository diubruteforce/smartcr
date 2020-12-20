package io.github.diubruteforce.smartcr.ui.profile.student

import io.github.diubruteforce.smartcr.model.ui.EmptyLoadingState
import io.github.diubruteforce.smartcr.model.ui.TypedSideEffectState
import io.github.diubruteforce.smartcr.ui.common.TextFieldState
import io.github.diubruteforce.smartcr.utils.base.BaseViewModel

data class StudentEditState(
    val fullName: TextFieldState = TextFieldState.FullNameState,
    val diuId: TextFieldState = TextFieldState.DiuIdState,
    val diuEmail: String? = null,
    val imageUrl: String? = "https://zoha131.github.io/images/profile.jpg",
    val gender: String? = null,
    val phoneNumber: TextFieldState = TextFieldState.PhoneState,
    val department: String? = null,
    val level: Int? = null,
    val term: Int? = null,
)

enum class StudentEditSuccess { Loaded, Saved }

class StudentEditViewModel :
    BaseViewModel<StudentEditState, TypedSideEffectState<Any, StudentEditSuccess, String>>(
        initialState = StudentEditState(),
        initialSideEffect = EmptyLoadingState
    ) {
    fun changeFullName(newName: String) = withState {
        val newState = fullName.copy(value = newName)
        setState { copy(fullName = newState) }
    }

    fun changeDiuId(newId: String) = withState {
        val newState = diuId.copy(value = newId)
        setState { copy(diuId = newState) }
    }

    fun changePhoneNumber(newNumber: String) = withState {
        val newState = phoneNumber.copy(value = newNumber)
        setState { copy(phoneNumber = newState) }
    }
}