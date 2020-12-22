package io.github.diubruteforce.smartcr.ui.profile.student

import androidx.hilt.lifecycle.ViewModelInject
import io.github.diubruteforce.smartcr.data.repository.AuthRepository
import io.github.diubruteforce.smartcr.model.data.Gender
import io.github.diubruteforce.smartcr.model.ui.InputState
import io.github.diubruteforce.smartcr.model.ui.TypedSideEffectState
import io.github.diubruteforce.smartcr.utils.base.BaseViewModel

data class StudentEditState(
    val fullName: InputState = InputState.FullNameState,
    val diuId: InputState = InputState.DiuIdState,
    val diuEmail: InputState = InputState.NotEmptyState.copy(value = "kudduseboyati14-8848@diu.edu.bd"),
    val imageUrl: String? = "https://zoha131.github.io/images/profile.jpg",
    val gender: InputState = InputState.NotEmptyState,
    val phoneNumber: InputState = InputState.PhoneState,
    val department: InputState = InputState.NotEmptyState,
    val level: InputState = InputState.NotEmptyState,
    val term: InputState = InputState.NotEmptyState,
)

enum class StudentEditSuccess { Loaded, Saved } //Student Earlier Profile load and Department Load

class StudentEditViewModel @ViewModelInject constructor(
    private val authRepository: AuthRepository
) : BaseViewModel<StudentEditState, TypedSideEffectState<Any, StudentEditSuccess, String>>(
    initialState = StudentEditState(),
    initialSideEffect = TypedSideEffectState.Uninitialized
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

    fun changeGender(newGender: Gender) = withState {
        val newState = gender.copy(value = newGender.toString())
        setState { copy(gender = newState) }
    }

    fun changeDepartment(newDepartment: Int) = withState {
        val newState = department.copy(value = newDepartment.toString())
        setState { copy(department = newState) }
    }

    fun changeLevel(newLevel: Int) = withState {
        val newState = level.copy(value = newLevel.toString())
        setState { copy(level = newState) }
    }

    fun changeTerm(newTerm: Int) = withState {
        val newState = term.copy(value = newTerm.toString())
        setState { copy(term = newState) }
    }

    fun saveProfile() = withState {
        val newName = fullName.validate()
        val newDiuId = diuId.validate()
        val newPhone = phoneNumber.validate()
        val newGender = gender.validate()
        val newDepartment = department.validate()
        val newLevel = level.validate()
        val newTerm = term.validate()

        setState {
            copy(
                fullName = newName,
                diuId = newDiuId,
                phoneNumber = newPhone,
                gender = newGender,
                department = newDepartment,
                level = newLevel,
                term = newTerm
            )
        }

        val isError = newName.isError || newDiuId.isError ||
                newPhone.isError || newGender.isError ||
                newDepartment.isError || newLevel.isError || newTerm.isError

        if (isError.not()) {

        }
    }
}