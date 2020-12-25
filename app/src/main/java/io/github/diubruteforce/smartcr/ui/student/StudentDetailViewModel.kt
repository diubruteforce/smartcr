package io.github.diubruteforce.smartcr.ui.student

import androidx.hilt.lifecycle.ViewModelInject
import io.github.diubruteforce.smartcr.data.repository.ProfileRepository
import io.github.diubruteforce.smartcr.model.data.Student
import io.github.diubruteforce.smartcr.model.ui.EmptyLoadingState
import io.github.diubruteforce.smartcr.model.ui.Error
import io.github.diubruteforce.smartcr.model.ui.TypedSideEffectState
import io.github.diubruteforce.smartcr.utils.base.BaseViewModel

data class StudentDetailState(
    val student: Student = Student()
)

enum class StudentDetailSuccess {
    Loaded, SignOut, Deleted
}

class StudentDetailViewModel @ViewModelInject constructor(
    private val profileRepository: ProfileRepository
) : BaseViewModel<StudentDetailState, Any, StudentDetailSuccess, String>(
    initialState = StudentDetailState()
) {
    fun loadData() {
        launchInViewModelScope {
            setSideEffect { EmptyLoadingState }

            val newStudent = profileRepository.getUserProfile()

            withState { setState { copy(student = newStudent) } }
            setSideEffect { TypedSideEffectState.Success(StudentDetailSuccess.Loaded) }
        }
    }

    fun signOut() {
        profileRepository.signOut()
        setSideEffect { TypedSideEffectState.Success(StudentDetailSuccess.SignOut) }
    }

    fun deleteProfile() {
        launchInViewModelScope {
            setSideEffect { EmptyLoadingState }

            profileRepository.deleteProfile()

            setSideEffect { TypedSideEffectState.Success(StudentDetailSuccess.Deleted) }
        }
    }

    override fun onCoroutineException(exception: Throwable) {
        setSideEffect { TypedSideEffectState.Fail(exception.message ?: String.Error) }
    }
}