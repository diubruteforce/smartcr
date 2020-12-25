package io.github.diubruteforce.smartcr.ui.teacher

import androidx.hilt.lifecycle.ViewModelInject
import io.github.diubruteforce.smartcr.data.repository.TeacherRepository
import io.github.diubruteforce.smartcr.model.data.Teacher
import io.github.diubruteforce.smartcr.model.ui.EmptyLoadingState
import io.github.diubruteforce.smartcr.model.ui.EmptySuccessState
import io.github.diubruteforce.smartcr.model.ui.InputState
import io.github.diubruteforce.smartcr.utils.base.StringFailSideEffectViewModel

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

    fun loadData() {
        launchInViewModelScope {
            setSideEffect { EmptyLoadingState }

            allTeacher = teacherRepository.getAllTeacher()

            withState {
                setState { copy(teacherList = allTeacher) }
            }

            setSideEffect { EmptySuccessState }
        }
    }

    fun changeQuery(newQuery: String) = withState {
        val newTeacherList = allTeacher.filter {
            it.fullName.contains(newQuery, true) ||
                    it.diuEmail.contains(newQuery, true) ||
                    it.initial.contains(newQuery, true)
        }.sortedBy { it.fullName }

        setState { copy(teacherList = newTeacherList, query = query.copy(value = newQuery)) }
    }
}