package io.github.diubruteforce.smartcr.ui.student

import androidx.hilt.lifecycle.ViewModelInject
import io.github.diubruteforce.smartcr.data.repository.ClassRepository
import io.github.diubruteforce.smartcr.data.repository.ProfileRepository
import io.github.diubruteforce.smartcr.model.data.Section
import io.github.diubruteforce.smartcr.model.data.Student
import io.github.diubruteforce.smartcr.model.ui.EmptyLoadingState
import io.github.diubruteforce.smartcr.model.ui.Error
import io.github.diubruteforce.smartcr.model.ui.TypedSideEffectState
import io.github.diubruteforce.smartcr.ui.common.SectionListItemState
import io.github.diubruteforce.smartcr.utils.base.BaseViewModel

data class StudentDetailState(
    val student: Student = Student(),
    val joinedSections: List<SectionListItemState> = emptyList()
)

enum class StudentDetailSuccess {
    Loaded, SignOut, Deleted, SectionJoined, SectionLeft
}

class StudentDetailViewModel @ViewModelInject constructor(
    private val profileRepository: ProfileRepository,
    private val classRepository: ClassRepository,
) : BaseViewModel<StudentDetailState, Any, StudentDetailSuccess, String>(
    initialState = StudentDetailState()
) {
    private lateinit var sectionList: List<Section>

    fun loadData() {
        launchInViewModelScope {
            setSideEffect { EmptyLoadingState }

            val newStudent = profileRepository.getUserProfile()
            sectionList = classRepository.getJoinedSectionList()

            populateSuccessState(newStudent)
            setSideEffect { TypedSideEffectState.Success(StudentDetailSuccess.Loaded) }
        }
    }

    private fun populateSuccessState(newStudent: Student) = withState {
        val joinedSectionList = sectionList.map {
            SectionListItemState(
                sectionId = it.id,
                courseCode = it.course.courseCode,
                name = "${it.course.courseCode} (${it.name})",
                isJoined = newStudent.joinedSection.contains(it.id)
            )
        }

        setState { copy(student = newStudent, joinedSections = joinedSectionList) }
    }

    fun joinSection(sectionId: String, courseCode: String) {
        launchInViewModelScope {
            setSideEffect { EmptyLoadingState }

            val profileData =
                classRepository.joinSection(sectionId = sectionId, courseCode = courseCode)
            populateSuccessState(profileData)

            setSideEffect { TypedSideEffectState.Success(StudentDetailSuccess.SectionJoined) }
        }
    }

    fun leaveSection(sectionId: String, courseCode: String) = launchInViewModelScope {
        setSideEffect { EmptyLoadingState }

        val profileData =
            classRepository.leaveSection(sectionId = sectionId, courseCode = courseCode)
        populateSuccessState(profileData)

        setSideEffect { TypedSideEffectState.Success(StudentDetailSuccess.SectionLeft) }
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