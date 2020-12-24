package io.github.diubruteforce.smartcr.ui.teacher

import androidx.hilt.lifecycle.ViewModelInject
import io.github.diubruteforce.smartcr.data.repository.TeacherRepository
import io.github.diubruteforce.smartcr.model.data.CounselingHourState
import io.github.diubruteforce.smartcr.model.data.Teacher
import io.github.diubruteforce.smartcr.model.data.Week
import io.github.diubruteforce.smartcr.model.ui.EmptyLoadingState
import io.github.diubruteforce.smartcr.model.ui.TypedSideEffectState
import io.github.diubruteforce.smartcr.utils.base.StringFailSideEffectViewModel

data class TeacherDetailState(
    val teacher: Teacher? = null,
    val day: Week = Week.Friday,
    val startTime: String = "",
    val endTime: String = "",
    val counselingHours: List<CounselingHourState> = emptyList()
)

class TeacherDetailViewModel @ViewModelInject constructor(
    private val teacherRepository: TeacherRepository
) : StringFailSideEffectViewModel<TeacherDetailState>(
    initialState = TeacherDetailState()
) {
    fun loadInitialData(teacherId: String) {
        setSideEffect { EmptyLoadingState }

        launchInViewModelScope {
            val teacher = teacherRepository.getTeacherProfile(teacherId)
            val counselingHours = teacherRepository.getCounselingHours(teacherId)

            withState {
                setState {
                    copy(
                        teacher = teacher,
                        counselingHours = counselingHours
                    )
                }
            }
            setSideEffect { TypedSideEffectState.Success(TeacherEditSuccess.Loaded) }
        }
    }

    fun startEditCounselingHour(state: CounselingHourState) = withState {
        setState {
            copy(
                startTime = state.startTime,
                endTime = state.endTime,
                day = Week.valueOf(state.day)
            )
        }
    }

    fun deleteProfile(teacherId: String) {

    }
}
