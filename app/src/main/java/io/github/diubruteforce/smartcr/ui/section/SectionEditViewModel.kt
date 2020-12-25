package io.github.diubruteforce.smartcr.ui.section

import androidx.hilt.lifecycle.ViewModelInject
import io.github.diubruteforce.smartcr.data.repository.ClassRepository
import io.github.diubruteforce.smartcr.data.repository.TeacherRepository
import io.github.diubruteforce.smartcr.model.data.Course
import io.github.diubruteforce.smartcr.model.data.Teacher
import io.github.diubruteforce.smartcr.model.ui.EmptyLoadingState
import io.github.diubruteforce.smartcr.model.ui.Error
import io.github.diubruteforce.smartcr.model.ui.InputState
import io.github.diubruteforce.smartcr.model.ui.TypedSideEffectState
import io.github.diubruteforce.smartcr.utils.base.BaseViewModel

data class SectionEditState(
    val sectionName: InputState = InputState.NotEmptyState,
    val facultyName: InputState = InputState.NotEmptyState,
    val courseName: InputState = InputState.NotEmptyState,
    val googleCode: InputState = InputState.NotEmptyState,
    val blcCode: InputState = InputState.NotEmptyState,
    val courseOutline: InputState = InputState.NotEmptyState,
    val teacherList: List<Teacher> = emptyList(),
    val courseList: List<Course> = emptyList()
)

enum class SectionEditSuccess {
    Loaded, Saved
}

class SectionEditViewModel @ViewModelInject constructor(
    private val classRepository: ClassRepository,
    private val teacherRepository: TeacherRepository
) : BaseViewModel<SectionEditState, Any, SectionEditSuccess, String>(
    initialState = SectionEditState(),
) {
    private var selectedCourse: Course? = null
    private var selectedTeacher: Teacher? = null

    fun loadDate() {
        launchInViewModelScope {
            setSideEffect { EmptyLoadingState }
            val courseList = classRepository.getCourseList()
            val teacherList = teacherRepository.getAllTeacher()

            withState {
                setState {
                    copy(teacherList = teacherList, courseList = courseList)
                }
            }

            setSideEffect { TypedSideEffectState.Success(SectionEditSuccess.Loaded) }
        }
    }

    fun changeTeacher(teacher: Teacher) = withState {
        selectedTeacher = teacher
        val newFacultyName = facultyName.copy(value = teacher.fullName)

        setState { copy(facultyName = newFacultyName) }
    }

    fun changeCourse(course: Course) = withState {
        selectedCourse = course
        val newCourseName = courseName.copy(value = course.courseTitle)

        setState { copy(courseName = newCourseName) }
    }

    fun changeSectionName(name: String) = withState {
        val newSectionName = sectionName.copy(value = name)

        setState { copy(sectionName = newSectionName) }
    }

    fun changeGoogleCode(code: String) = withState {
        val newGoogleCode = googleCode.copy(value = code)

        setState { copy(googleCode = newGoogleCode) }
    }

    fun changeBlcCode(code: String) = withState {
        val newBlcCode = blcCode.copy(value = code)

        setState { copy(blcCode = newBlcCode) }
    }

    fun changeCourseOutline(outline: String) = withState {
        val newOutline = courseOutline.copy(value = outline)

        setState { copy(courseName = newOutline) }
    }


    override fun onCoroutineException(exception: Throwable) {
        setSideEffect {
            TypedSideEffectState.Fail(exception.message ?: String.Error)
        }
    }
}