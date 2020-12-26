package io.github.diubruteforce.smartcr.ui.section

import androidx.hilt.lifecycle.ViewModelInject
import io.github.diubruteforce.smartcr.data.repository.ClassRepository
import io.github.diubruteforce.smartcr.data.repository.TeacherRepository
import io.github.diubruteforce.smartcr.model.data.*
import io.github.diubruteforce.smartcr.model.ui.EmptyLoadingState
import io.github.diubruteforce.smartcr.model.ui.Error
import io.github.diubruteforce.smartcr.model.ui.InputState
import io.github.diubruteforce.smartcr.model.ui.TypedSideEffectState
import io.github.diubruteforce.smartcr.utils.base.BaseViewModel

data class SectionEditState(
    val sectionName: InputState = InputState.NotEmptyState,
    val instructorName: InputState = InputState.NotEmptyState,
    val courseTitle: InputState = InputState.NotEmptyState,
    val googleCode: InputState = InputState.EmptyState,
    val blcCode: InputState = InputState.EmptyState,
    val courseOutline: InputState = InputState.EmptyState,
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
    private lateinit var sectionData: Section
    private lateinit var selectedCourse: Course
    private lateinit var selectedInstructor: Instructor

    fun loadDate(sectionId: String?) {
        launchInViewModelScope {
            setSideEffect { EmptyLoadingState }

            sectionData = classRepository.getSectionData(sectionId)
            selectedCourse = sectionData.course
            selectedInstructor = sectionData.instructor

            val courseList = classRepository.getCourseList()
            val teacherList = teacherRepository.getAllTeacher()

            withState {
                setState {
                    copy(
                        sectionName = sectionName.copy(value = sectionData.name),
                        instructorName = instructorName.copy(value = sectionData.instructor.name),
                        courseTitle = courseTitle.copy(value = sectionData.course.courseTitle),
                        googleCode = googleCode.copy(value = sectionData.googleCode),
                        blcCode = blcCode.copy(value = sectionData.blcCode),
                        courseOutline = courseOutline.copy(value = sectionData.courseOutline),
                        teacherList = teacherList,
                        courseList = courseList
                    )
                }
            }

            setSideEffect { TypedSideEffectState.Success(SectionEditSuccess.Loaded) }
        }
    }

    fun changeTeacher(teacher: Teacher) = withState {
        selectedInstructor = teacher.toInstructor()
        val newFacultyName = instructorName.copy(value = teacher.fullName)

        setState { copy(instructorName = newFacultyName) }
    }

    fun canChangeCourseOrName(): Boolean = sectionData.id.isEmpty()

    fun changeCourse(course: Course) = withState {
        selectedCourse = course
        val newCourseName = courseTitle.copy(value = course.courseTitle)

        setState { copy(courseTitle = newCourseName) }
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

        setState { copy(courseTitle = newOutline) }
    }

    fun saveSection() = withState {
        val newSectionName = sectionName.validate()
        val newCourseTitle = courseTitle.validate()
        val newInstructorName = instructorName.validate()

        val isError = newSectionName.isError ||
                newCourseTitle.isError ||
                newInstructorName.isError

        setState {
            copy(
                sectionName = newSectionName,
                courseTitle = newCourseTitle,
                instructorName = newInstructorName
            )
        }

        if (isError.not()) {
            launchInViewModelScope {
                setSideEffect { EmptyLoadingState }

                val newSection = sectionData.copy(
                    name = newSectionName.value,
                    course = selectedCourse,
                    instructor = selectedInstructor,
                    googleCode = googleCode.value,
                    blcCode = blcCode.value,
                    courseOutline = courseOutline.value
                )

                classRepository.saveSection(newSection)

                setSideEffect { TypedSideEffectState.Success(SectionEditSuccess.Saved) }
            }
        }
    }


    override fun onCoroutineException(exception: Throwable) {
        setSideEffect {
            TypedSideEffectState.Fail(exception.message ?: String.Error)
        }
    }
}