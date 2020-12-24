package io.github.diubruteforce.smartcr.ui.student

import androidx.hilt.lifecycle.ViewModelInject
import io.github.diubruteforce.smartcr.data.repository.ProfileRepository
import io.github.diubruteforce.smartcr.model.data.Student
import io.github.diubruteforce.smartcr.model.ui.EmptyLoadingState
import io.github.diubruteforce.smartcr.utils.base.StringFailSideEffectViewModel

data class StudentDetailState(
    val student: Student = Student()
)

class StudentDetailViewModel @ViewModelInject constructor(
    private val profileRepository: ProfileRepository
) : StringFailSideEffectViewModel<StudentDetailState>(
    initialState = StudentDetailState()
) {
    init {
        launchInViewModelScope {
            setSideEffect { EmptyLoadingState }

            val newStudent = profileRepository.getUserProfile()

            withState { setState { copy(student = newStudent) } }
        }
    }
}