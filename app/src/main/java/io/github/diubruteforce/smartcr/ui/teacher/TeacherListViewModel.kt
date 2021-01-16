package io.github.diubruteforce.smartcr.ui.teacher

import androidx.hilt.lifecycle.ViewModelInject
import io.github.diubruteforce.smartcr.data.repository.TeacherRepository
import io.github.diubruteforce.smartcr.model.data.Teacher
import io.github.diubruteforce.smartcr.model.ui.EmptyLoadingState
import io.github.diubruteforce.smartcr.model.ui.EmptySuccessState
import io.github.diubruteforce.smartcr.model.ui.InputState
import io.github.diubruteforce.smartcr.utils.base.StringFailSideEffectViewModel
import io.github.diubruteforce.smartcr.utils.extension.filterByQuery
import kotlinx.coroutines.flow.collect

data class TeacherListState(
    val teacherList: List<Teacher> = emptyList(),
    val query: InputState = InputState.EmptyState
)

class TeacherListViewModel @ViewModelInject constructor(
    private val teacherRepository: TeacherRepository
) : StringFailSideEffectViewModel<TeacherListState>(
    initialState = TeacherListState()
) {
    private var allTeacher: List<Teacher> = emptyList()

    init {
        setSideEffect { EmptyLoadingState }

        launchInViewModelScope {
            teacherRepository.teacherListFlow.collect { teacherList ->
                allTeacher = teacherList

                withState {
                    setState { copy(teacherList = allTeacher.filterByQuery(query.value)) }
                }

                if (allTeacher.isNotEmpty()) setSideEffect { EmptySuccessState }
            }
        }
    }

    fun changeQuery(newQuery: String) = withState {
        val newTeacherList = allTeacher.filterByQuery(newQuery)

        setState { copy(teacherList = newTeacherList, query = query.copy(value = newQuery)) }
    }
}