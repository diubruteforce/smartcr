package io.github.diubruteforce.smartcr.ui.section

import androidx.hilt.lifecycle.ViewModelInject
import io.github.diubruteforce.smartcr.data.repository.ClassRepository
import io.github.diubruteforce.smartcr.model.data.Course
import io.github.diubruteforce.smartcr.model.data.Section
import io.github.diubruteforce.smartcr.model.data.Student
import io.github.diubruteforce.smartcr.model.ui.EmptyLoadingState
import io.github.diubruteforce.smartcr.model.ui.Error
import io.github.diubruteforce.smartcr.model.ui.TypedSideEffectState
import io.github.diubruteforce.smartcr.ui.common.SectionListItemState
import io.github.diubruteforce.smartcr.utils.base.BaseViewModel

data class SectionListState(
    val sections: List<SectionListItemState> = emptyList(),
    val course: Course = Course()
)

enum class SectionListSuccess {
    Loaded, Joined, Left
}

class SectionListViewModel @ViewModelInject constructor(
    private val classRepository: ClassRepository
) : BaseViewModel<SectionListState, Any, SectionListSuccess, String>(
    initialState = SectionListState(),
) {
    private lateinit var allSections: List<Section>
    private lateinit var profileData: Student
    private var joinedInAnotherSection: Boolean = false

    fun loadData(courseId: String) {
        launchInViewModelScope {
            setSideEffect { EmptyLoadingState }

            profileData = classRepository.getUserProfile()
            allSections = classRepository.getSectionList(courseId)

            val sections = populateSectionListItemState()
            val course = classRepository.getCourse(courseId)

            withState {
                setState {
                    copy(sections = sections, course = course)
                }
            }

            setSideEffect { TypedSideEffectState.Success(SectionListSuccess.Loaded) }
        }
    }

    private fun populateSectionListItemState(): List<SectionListItemState> = allSections.map {
        val isJoined = profileData.joinedSection.contains(it.id)

        if (isJoined) joinedInAnotherSection = true

        SectionListItemState(name = it.name, isJoined = isJoined, courseId = it.id)
    }

    fun joinSection(sectionId: String) {
        if (joinedInAnotherSection) setSideEffect {
            TypedSideEffectState.Fail("You have already joined in another section. Please leave from that section before joining a new section")
        } else launchInViewModelScope {
            setSideEffect { EmptyLoadingState }

            profileData = classRepository.joinSection(sectionId)

            withState {
                setState {
                    copy(sections = populateSectionListItemState())
                }
            }

            joinedInAnotherSection = true
            setSideEffect { TypedSideEffectState.Success(SectionListSuccess.Joined) }
        }
    }

    fun leaveSection(sectionId: String) = launchInViewModelScope {
        setSideEffect { EmptyLoadingState }

        profileData = classRepository.leaveSection(sectionId)

        withState {
            setState {
                copy(sections = populateSectionListItemState())
            }
        }
        joinedInAnotherSection = false
        setSideEffect { TypedSideEffectState.Success(SectionListSuccess.Loaded) }
    }

    override fun onCoroutineException(exception: Throwable) {
        setSideEffect { TypedSideEffectState.Fail(exception.message ?: String.Error) }
    }
}