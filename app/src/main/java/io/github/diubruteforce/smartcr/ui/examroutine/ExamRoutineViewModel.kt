package io.github.diubruteforce.smartcr.ui.examroutine

import androidx.hilt.lifecycle.ViewModelInject
import io.github.diubruteforce.smartcr.data.repository.ExtraFeatureRepository
import io.github.diubruteforce.smartcr.model.data.Course
import io.github.diubruteforce.smartcr.model.data.ExamRoutine
import io.github.diubruteforce.smartcr.model.ui.EmptyLoadingState
import io.github.diubruteforce.smartcr.model.ui.EmptySuccessState
import io.github.diubruteforce.smartcr.model.ui.InputState
import io.github.diubruteforce.smartcr.utils.base.StringFailSideEffectViewModel
import io.github.diubruteforce.smartcr.utils.extension.toDateTimeMillis
import java.util.*

data class ExamRoutineState(
    val routines: List<ExamRoutine> = emptyList(),
    val editingExamRoutine: ExamRoutine? = null,
    val course: InputState = InputState.NotEmptyState,
    val date: InputState = InputState.NotEmptyState,
    val time: InputState = InputState.NotEmptyState,
)

class ExamRoutineViewModel @ViewModelInject constructor(
    private val extraFeatureRepository: ExtraFeatureRepository
) : StringFailSideEffectViewModel<ExamRoutineState>(
    initialState = ExamRoutineState()
) {
    var selectedCourse: Course? = null
    val currentDate: Calendar = Calendar.getInstance(Locale.getDefault())
    var courseList: List<Course> = emptyList()

    fun loadData() = launchInViewModelScope {
        setSideEffect { EmptyLoadingState }

        val examRoutineList = extraFeatureRepository.getExamRoutineList(currentDate.timeInMillis)
        if (courseList.isEmpty()) courseList = extraFeatureRepository.getJoinedCourseList()

        withState { setState { copy(editingExamRoutine = null, routines = examRoutineList) } }

        setSideEffect { EmptySuccessState }
    }

    fun canEdit() = courseList.isNotEmpty()

    fun startEditing(examRoutine: ExamRoutine) = withState {
        selectedCourse = courseList.find { it.id == examRoutine.courseId }

        setState {
            copy(
                editingExamRoutine = examRoutine,
                course = course.copy(value = examRoutine.courseCode),
                date = date.copy(value = examRoutine.date),
                time = time.copy(value = examRoutine.time)
            )
        }
    }

    fun cancelEditing() = withState { setState { copy(editingExamRoutine = null) } }

    fun changeCourse(newCourse: Course) = withState {
        selectedCourse = newCourse

        setState { copy(course = course.copy(value = newCourse.courseCode)) }
    }

    fun changeDate(newDate: String) = withState {
        setState { copy(date = date.copy(value = newDate)) }
    }

    fun changeTime(newTime: String) = withState {
        setState { copy(time = time.copy(value = newTime)) }
    }

    fun saveExamRoutine() = withState {
        val newCourse = course.validate()
        val newDate = date.validate()
        val newTime = time.validate()

        setState {
            copy(
                course = newCourse,
                date = newDate,
                time = newTime
            )
        }

        val selectedCourse = selectedCourse

        val isError = newCourse.isError ||
                newDate.isError ||
                newTime.isError

        if (isError.not() && selectedCourse != null && editingExamRoutine != null) launchInViewModelScope {
            setSideEffect { EmptyLoadingState }

            val newRoutine = editingExamRoutine.copy(
                courseId = selectedCourse.id,
                courseCode = selectedCourse.courseCode,
                courseName = selectedCourse.courseTitle,
                date = newDate.value,
                time = newTime.value,
                dateTimeMillis = "${newDate.value} 11:59 PM".toDateTimeMillis()
            )

            extraFeatureRepository.saveExamRoutine(newRoutine)

            loadData()
        }
    }

    fun deleteExamRoutine(examRoutine: ExamRoutine) = launchInViewModelScope {
        setSideEffect { EmptyLoadingState }

        val newExamRoutine = examRoutine.copy(isActive = false)

        extraFeatureRepository.saveExamRoutine(newExamRoutine)

        loadData()
    }
}