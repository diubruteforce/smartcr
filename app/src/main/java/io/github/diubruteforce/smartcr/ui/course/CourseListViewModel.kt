package io.github.diubruteforce.smartcr.ui.course

import androidx.hilt.lifecycle.ViewModelInject
import io.github.diubruteforce.smartcr.data.repository.ClassRepository
import io.github.diubruteforce.smartcr.model.data.Course
import io.github.diubruteforce.smartcr.model.ui.EmptyLoadingState
import io.github.diubruteforce.smartcr.model.ui.EmptySuccessState
import io.github.diubruteforce.smartcr.utils.base.StringFailSideEffectViewModel

data class CourseListState(
    val courses: Map<String, List<Course>> = emptyMap()
)

class CourseListViewModel @ViewModelInject constructor(
    private val classRepository: ClassRepository
) : StringFailSideEffectViewModel<CourseListState>(
    initialState = CourseListState(),
) {
    fun loadData() {
        launchInViewModelScope {
            setSideEffect { EmptyLoadingState }

            val courseList = classRepository.getCourseList()

            val courseMap = courseList.groupBy { "Level ${it.level} Term ${it.term}" }.toSortedMap()

            withState { setState { copy(courses = courseMap) } }
            setSideEffect { EmptySuccessState }
        }
    }
}