package io.github.diubruteforce.smartcr.ui.section

import androidx.hilt.lifecycle.ViewModelInject
import io.github.diubruteforce.smartcr.data.repository.ClassRepository
import io.github.diubruteforce.smartcr.model.data.Routine
import io.github.diubruteforce.smartcr.model.data.Section
import io.github.diubruteforce.smartcr.model.data.Week
import io.github.diubruteforce.smartcr.model.ui.EmptyLoadingState
import io.github.diubruteforce.smartcr.model.ui.EmptySuccessState
import io.github.diubruteforce.smartcr.model.ui.InputState
import io.github.diubruteforce.smartcr.model.ui.TypedSideEffectState
import io.github.diubruteforce.smartcr.utils.base.StringFailSideEffectViewModel

data class SectionDetailState(
    val section: Section = Section(),
    val routines: List<Routine> = emptyList(),
    val editingRoutine: Routine? = null,
    val room: InputState = InputState.NotEmptyState,
    val day: InputState = InputState.NotEmptyState,
    val startTime: InputState = InputState.NotEmptyState,
    val endTime: InputState = InputState.NotEmptyState,
)

class SectionDetailViewModel @ViewModelInject constructor(
    private val classRepository: ClassRepository
) : StringFailSideEffectViewModel<SectionDetailState>(
    initialState = SectionDetailState(),
) {
    fun loadData(sectionId: String) = launchInViewModelScope {
        setSideEffect { EmptyLoadingState }

        val section = classRepository.getSectionData(sectionId)
        val routineList = classRepository.getSectionRoutineList(sectionId)

        withState {
            setState {
                copy(section = section, routines = routineList)
            }
        }

        setSideEffect { EmptySuccessState }
    }

    fun startEditingRoutine(routine: Routine) = withState {
        launchInViewModelScope {
            if (canEditSection()) {
                setState {
                    copy(
                        editingRoutine = routine,
                        room = room.copy(value = routine.room),
                        day = day.copy(value = routine.day),
                        startTime = startTime.copy(value = routine.startTime),
                        endTime = endTime.copy(value = routine.endTime)
                    )
                }
            }
        }
    }

    fun changeRoom(newRoom: String) = withState {
        val newState = room.copy(value = newRoom)
        setState { copy(room = newState) }
    }

    fun changeDay(newDay: Week) = withState {
        val newState = day.copy(value = newDay.name)
        setState { copy(day = newState) }
    }

    fun changeStartTime(newTime: String) = withState {
        val newState = startTime.copy(value = newTime)
        setState { copy(startTime = newState) }
    }

    fun changeEndTime(newTime: String) = withState {
        val newState = endTime.copy(value = newTime)
        setState { copy(endTime = newState) }
    }

    fun cancelEditing() = withState {
        setState { copy(editingRoutine = null) }
    }

    fun saveRoutine() = withState {
        val newRoom = room.validate()
        val newDay = day.validate()
        val newStartTime = startTime.validate()
        val newEndTime = endTime.validate()

        val isError = newRoom.isError ||
                newDay.isError ||
                newStartTime.isError ||
                newEndTime.isError

        setState {
            copy(
                room = newRoom,
                day = newDay,
                startTime = newStartTime,
                endTime = newEndTime
            )
        }

        if (isError.not() && editingRoutine != null) launchInViewModelScope {
            setSideEffect { EmptyLoadingState }

            val newRoutine = editingRoutine.copy(
                room = newRoom.value,
                day = newDay.value,
                startTime = newStartTime.value,
                endTime = newEndTime.value,
                sectionId = section.id,
                sectionName = section.name,
                courseCode = section.course.courseCode,
                courseName = section.course.courseTitle
            )

            classRepository.saveRoutine(newRoutine)

            val routineList = classRepository.getSectionRoutineList(section.id)

            setState { copy(routines = routineList, editingRoutine = null) }
            setSideEffect { EmptySuccessState }
        }
    }

    fun deleteRoutine(routine: Routine) = withState {
        launchInViewModelScope {
            setSideEffect { EmptyLoadingState }
            classRepository.deleteRoutine(routine.id)

            val routineList = classRepository.getSectionRoutineList(section.id)
            setState { copy(routines = routineList, editingRoutine = null) }
            setSideEffect { EmptySuccessState }
        }
    }

    suspend fun canEditSection(): Boolean {
        val isMember =
            classRepository.getUserProfile().joinedSection.contains(state.value.section.id)

        if (isMember.not()) setSideEffect {
            TypedSideEffectState.Fail(
                "You have not joined this section. To edit you must have to join this section"
            )
        }

        return isMember
    }
}