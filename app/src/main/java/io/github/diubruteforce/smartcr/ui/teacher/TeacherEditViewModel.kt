package io.github.diubruteforce.smartcr.ui.teacher

import android.net.Uri
import androidx.hilt.lifecycle.ViewModelInject
import io.github.diubruteforce.smartcr.data.repository.ClassRepository
import io.github.diubruteforce.smartcr.data.repository.TeacherRepository
import io.github.diubruteforce.smartcr.model.data.Department
import io.github.diubruteforce.smartcr.model.data.Designation
import io.github.diubruteforce.smartcr.model.data.Gender
import io.github.diubruteforce.smartcr.model.data.Teacher
import io.github.diubruteforce.smartcr.model.ui.EmptyLoadingState
import io.github.diubruteforce.smartcr.model.ui.Error
import io.github.diubruteforce.smartcr.model.ui.InputState
import io.github.diubruteforce.smartcr.model.ui.TypedSideEffectState
import io.github.diubruteforce.smartcr.utils.base.BaseViewModel
import io.github.diubruteforce.smartcr.utils.extension.OptionalPhoneValidator

data class TeacherEditState(
    val fullName: InputState = InputState.FullNameState,
    val initial: InputState = InputState.EmptyState,
    val diuEmail: InputState = InputState.DiuEmailState,
    val profileUrl: String = "",
    val gender: InputState = InputState.NotEmptyState,
    val phone: InputState = InputState.PhoneState.copy(validator = Regex.OptionalPhoneValidator),
    val department: InputState = InputState.NotEmptyState,
    val room: InputState = InputState.EmptyState,
    val designation: InputState = InputState.NotEmptyState,
    val departmentList: List<Department> = emptyList()
)

enum class TeacherEditSuccess { Loaded, ProfileSaved, ImageSaved }

class TeacherEditViewModel @ViewModelInject constructor(
    private val teacherRepository: TeacherRepository,
    private val classRepository: ClassRepository
) : BaseViewModel<TeacherEditState, Any, TeacherEditSuccess, String>(
    initialState = TeacherEditState()
) {
    private var storedDepartment: Department? = null

    fun loadInitialData(teacherId: String?) {
        setSideEffect { EmptyLoadingState }

        launchInViewModelScope {
            val departmentList = classRepository.getAllDepartment()
            val profile = teacherRepository.getTeacherProfile(teacherId)

            storedDepartment = departmentList.find { it.codeName == profile.departmentCode }

            withState {
                setState {
                    copy(
                        fullName = fullName.copy(value = profile.fullName),
                        initial = initial.copy(value = profile.initial),
                        diuEmail = diuEmail.copy(value = profile.diuEmail),
                        profileUrl = profile.profileUrl,
                        gender = gender.copy(value = profile.gender),
                        phone = phone.copy(value = profile.phone),
                        department = department.copy(value = profile.departmentCode),
                        room = room.copy(value = profile.room),
                        designation = designation.copy(value = profile.designation),
                        departmentList = departmentList
                    )
                }
            }
            setSideEffect { TypedSideEffectState.Success(TeacherEditSuccess.Loaded) }
        }
    }

    fun changeFullName(newName: String) = withState {
        val newState = fullName.copy(value = newName)
        setState { copy(fullName = newState) }
    }

    fun changeInitial(newInitial: String) = withState {
        val newState = initial.copy(value = newInitial)
        setState { copy(initial = newState) }
    }

    fun changeDiuEmail(newDiuEmail: String) = withState {
        val newState = diuEmail.copy(value = newDiuEmail)
        setState { copy(diuEmail = newState) }
    }

    fun changeGender(newGender: Gender) = withState {
        val newState = gender.copy(value = newGender.name)
        setState { copy(gender = newState) }
    }

    fun changePhoneNumber(newNumber: String) = withState {
        val newState = phone.copy(value = newNumber)
        setState { copy(phone = newState) }
    }

    fun changeDepartment(newDepartment: Department) = withState {
        storedDepartment = newDepartment
        val newState = department.copy(value = newDepartment.codeName)
        setState { copy(department = newState) }
    }

    fun changeRoom(newRoom: String) = withState {
        val newState = room.copy(value = newRoom)
        setState { copy(room = newState) }
    }

    fun changeDesignation(newDesignation: Designation) = withState {
        val newState = designation.copy(value = newDesignation.title)
        setState { copy(designation = newState) }
    }

    fun uploadImage(uri: Uri?) = withState {
        if (uri != null) {
            setSideEffect { EmptyLoadingState }

            launchInViewModelScope {
                val imageUrl = teacherRepository.uploadProfileImage(uri)

                setState { copy(profileUrl = imageUrl) }
                setSideEffect { TypedSideEffectState.Success(TeacherEditSuccess.ImageSaved) }
            }
        } else {
            setSideEffect { TypedSideEffectState.Fail("Unable to upload to image.") }
        }
    }

    fun saveProfile() = withState {
        val newFullName = fullName.validate()
        val newInitial = initial.validate()
        val newDiuEmail = diuEmail.validate()
        val newGender = gender.validate()
        val newPhoneNumber = phone.validate()
        val newDepartment = department.validate()
        val newRoom = room.validate()
        val newDesignation = designation.validate()

        setState {
            copy(
                fullName = newFullName,
                initial = newInitial,
                diuEmail = newDiuEmail,
                gender = newGender,
                phone = newPhoneNumber,
                department = newDepartment,
                room = newRoom,
                designation = newDesignation
            )
        }

        val isError = newFullName.isError ||
                newInitial.isError ||
                newDiuEmail.isError ||
                newGender.isError ||
                newPhoneNumber.isError ||
                newDepartment.isError ||
                newRoom.isError ||
                newDesignation.isError

        val storedDepartment = storedDepartment

        if (isError.not() && storedDepartment != null) {
            setSideEffect { EmptyLoadingState }
            launchInViewModelScope {
                val canSave = teacherRepository.canSave(diuEmail.value)
                if (canSave.not()) {
                    setSideEffect { TypedSideEffectState.Fail("We already have a teacher with the same email in our database.") }
                    return@launchInViewModelScope
                }

                val teacher = Teacher(
                    fullName = fullName.value,
                    profileUrl = profileUrl,
                    initial = initial.value,
                    diuEmail = diuEmail.value,
                    gender = gender.value,
                    phone = phone.value,
                    departmentId = storedDepartment.id,
                    departmentCode = storedDepartment.codeName,
                    departmentName = storedDepartment.name,
                    room = room.value,
                    designation = designation.value,
                )

                teacherRepository.saveTeacherProfile(teacher)

                setSideEffect { TypedSideEffectState.Success(TeacherEditSuccess.ProfileSaved) }
            }
        } else {
            setSideEffect { TypedSideEffectState.Fail("Some of your inputs are invalid. Please enter right information.") }
        }
    }

    override fun onCoroutineException(exception: Throwable) {
        setSideEffect { TypedSideEffectState.Fail(exception.message ?: String.Error) }
    }
}