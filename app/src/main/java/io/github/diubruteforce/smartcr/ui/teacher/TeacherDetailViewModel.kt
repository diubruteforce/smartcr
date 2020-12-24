package io.github.diubruteforce.smartcr.ui.teacher

import androidx.hilt.lifecycle.ViewModelInject
import io.github.diubruteforce.smartcr.data.repository.TeacherRepository
import io.github.diubruteforce.smartcr.model.data.CounselingHourState
import io.github.diubruteforce.smartcr.model.data.Teacher
import io.github.diubruteforce.smartcr.model.data.Week
import io.github.diubruteforce.smartcr.model.ui.EmptyLoadingState
import io.github.diubruteforce.smartcr.model.ui.Error
import io.github.diubruteforce.smartcr.model.ui.InputState
import io.github.diubruteforce.smartcr.model.ui.TypedSideEffectState
import io.github.diubruteforce.smartcr.utils.base.BaseViewModel

data class TeacherDetailState(
    val teacher: Teacher? = null,
    val day: InputState = InputState.NotEmptyState,
    val startTime: InputState = InputState.NotEmptyState,
    val endTime: InputState = InputState.NotEmptyState,
    val isEditMode: Boolean = false,
    val editTitle: String = "",
    val counselingHours: List<CounselingHourState> = emptyList()
)

enum class TeacherDetailSuccess {
    ProfileLoaded, CounselingSaved, ProfileDeleted
}

class TeacherDetailViewModel @ViewModelInject constructor(
    private val teacherRepository: TeacherRepository
) : BaseViewModel<TeacherDetailState, Any, TeacherDetailSuccess, String>(
    initialState = TeacherDetailState()
) {
    private var counselingHourId: String? = null

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
            setSideEffect { TypedSideEffectState.Success(TeacherDetailSuccess.ProfileLoaded) }
        }
    }

    fun deleteProfile() = withState {
        require(teacher != null)

        setSideEffect { EmptyLoadingState }

        launchInViewModelScope {
            teacherRepository.deleteTeacherProfile(teacher)

            setSideEffect { TypedSideEffectState.Success(TeacherDetailSuccess.ProfileDeleted) }
        }
    }

    fun startEditCounselingHour(state: CounselingHourState) = withState {
        counselingHourId = state.id
        setState {
            copy(
                isEditMode = true,
                editTitle = "Edit Counseling Hour",
                startTime = startTime.copy(value = state.startTime),
                endTime = endTime.copy(value = state.endTime),
                day = day.copy(value = state.day)
            )
        }
    }

    fun startAddCounselingHour() = withState {
        counselingHourId = null
        setState {
            copy(
                isEditMode = true,
                editTitle = "Add Counseling Hour",
                startTime = startTime.copy(value = "08:00 AM"),
                endTime = endTime.copy(value = "11:00 AM"),
                day = day.copy(value = Week.Saturday.name)
            )
        }
    }

    fun changeDay(newDay: Week) = withState {
        val newDayState = day.copy(value = newDay.name)
        setState { copy(day = newDayState) }
    }

    fun changeStartTime(newTime: String) = withState {
        val newStartTime = startTime.copy(value = newTime)
        setState { copy(startTime = newStartTime) }
    }

    fun changeEndTime(newTime: String) = withState {
        val newEndTime = endTime.copy(value = newTime)
        setState { copy(endTime = newEndTime) }
    }

    fun cancelCounselingEditing() = withState {
        setState { copy(isEditMode = false) }
    }

    fun saveCounselingHour() = withState {
        setSideEffect { EmptyLoadingState }

        val counselingHour = CounselingHourState(
            id = counselingHourId ?: "",
            day = day.value,
            startTime = startTime.value,
            endTime = endTime.value
        )

        launchInViewModelScope {
            teacherRepository.saveCounselingHour(counselingHour)
            val counselingHourList = teacherRepository.getCounselingHours(teacher?.id!!)

            setState { copy(isEditMode = false, counselingHours = counselingHourList) }
            setSideEffect { TypedSideEffectState.Success(TeacherDetailSuccess.CounselingSaved) }
        }
    }

    fun deleteCounselingHour(state: CounselingHourState) = withState {
        setSideEffect { EmptyLoadingState }

        launchInViewModelScope {
            teacherRepository.deleteCounselingHour(state)
            val counselingHourList = teacherRepository.getCounselingHours(teacher?.id!!)

            setState { copy(isEditMode = false, counselingHours = counselingHourList) }
            setSideEffect { TypedSideEffectState.Success(TeacherDetailSuccess.CounselingSaved) }
        }
    }

    override fun onCoroutineException(exception: Throwable) {
        setSideEffect { TypedSideEffectState.Fail(exception.message ?: String.Error) }
    }
}
