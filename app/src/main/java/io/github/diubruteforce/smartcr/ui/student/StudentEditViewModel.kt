package io.github.diubruteforce.smartcr.ui.student

import android.net.Uri
import androidx.hilt.lifecycle.ViewModelInject
import io.github.diubruteforce.smartcr.data.repository.ProfileRepository
import io.github.diubruteforce.smartcr.model.data.Department
import io.github.diubruteforce.smartcr.model.data.Gender
import io.github.diubruteforce.smartcr.model.data.Student
import io.github.diubruteforce.smartcr.model.ui.EmptyLoadingState
import io.github.diubruteforce.smartcr.model.ui.Error
import io.github.diubruteforce.smartcr.model.ui.InputState
import io.github.diubruteforce.smartcr.model.ui.TypedSideEffectState
import io.github.diubruteforce.smartcr.utils.base.BaseViewModel
import timber.log.Timber

data class StudentEditState(
    val fullName: InputState = InputState.FullNameState,
    val diuId: InputState = InputState.DiuIdState,
    val diuEmail: InputState = InputState.NotEmptyState,
    val imageUrl: String = "",
    val gender: InputState = InputState.NotEmptyState,
    val phoneNumber: InputState = InputState.PhoneState,
    val department: InputState = InputState.NotEmptyState,
    val level: InputState = InputState.NotEmptyState,
    val term: InputState = InputState.NotEmptyState,
    val departmentList: List<Department> = emptyList()
)

enum class StudentEditSuccess { Loaded, ProfileSaved, ImageSaved }

class StudentEditViewModel @ViewModelInject constructor(
    private val profileRepository: ProfileRepository
) : BaseViewModel<StudentEditState, TypedSideEffectState<Any, StudentEditSuccess, String>>(
    initialState = StudentEditState(),
    initialSideEffect = TypedSideEffectState.Uninitialized
) {
    private var storedDepartment: Department? = null

    init {
        setSideEffect { EmptyLoadingState }

        launchInViewModelScope {
            val departmentList = profileRepository.getAllDepartment()
            val profile = profileRepository.getUserProfile()

            storedDepartment = departmentList.find { it.codeName == profile.departmentCode }

            withState {
                setState {
                    copy(
                        fullName = fullName.copy(value = profile.fullName),
                        diuId = diuId.copy(value = profile.diuId),
                        diuEmail = diuEmail.copy(value = profile.diuEmail),
                        imageUrl = profile.profileUrl,
                        gender = gender.copy(value = profile.gender),
                        phoneNumber = phoneNumber.copy(value = profile.phone),
                        department = department.copy(value = profile.departmentCode),
                        level = level.copy(value = profile.level),
                        term = term.copy(value = profile.term),
                        departmentList = departmentList
                    )
                }
            }
            setSideEffect { TypedSideEffectState.Success(StudentEditSuccess.Loaded) }
        }
    }

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

    fun changeDepartment(newDepartment: Department) = withState {
        storedDepartment = newDepartment
        val newState = department.copy(value = newDepartment.codeName)
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

    fun uploadImage(uri: Uri?) = withState {
        if (uri != null) {
            setSideEffect { EmptyLoadingState }

            launchInViewModelScope {
                val imageUrl = profileRepository.uploadProfileImage(uri)

                setState { copy(imageUrl = imageUrl) }
                setSideEffect { TypedSideEffectState.Success(StudentEditSuccess.ImageSaved) }
            }
        } else {
            setSideEffect { TypedSideEffectState.Fail("Unable to upload to image.") }
        }
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

        val storedDepartment = storedDepartment
        if (isError.not() && storedDepartment != null) {
            setSideEffect { EmptyLoadingState }
            launchInViewModelScope {
                try {
                    val student = Student(
                        fullName = fullName.value,
                        diuId = diuId.value,
                        diuEmail = diuEmail.value,
                        phone = phoneNumber.value,
                        gender = gender.value,
                        departmentCode = storedDepartment.codeName,
                        departmentId = storedDepartment.id,
                        departmentName = storedDepartment.name,
                        term = term.value,
                        level = level.value,
                        profileUrl = imageUrl,
                        batch = diuId.value.take(3)
                    )
                    profileRepository.saveUserProfile(student)


                    setSideEffect { TypedSideEffectState.Success(StudentEditSuccess.ProfileSaved) }
                } catch (ex: Exception) {
                    Timber.e(ex)
                    setSideEffect { TypedSideEffectState.Fail(ex.message ?: String.Error) }
                }
            }
        } else {
            setSideEffect { TypedSideEffectState.Fail("Some of your inputs are invalid. Please enter right information.") }
        }
    }
}