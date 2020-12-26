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
import io.github.diubruteforce.smartcr.utils.extension.filterByQuery
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

data class SectionEditState(
    val sectionName: InputState = InputState.NotEmptyState,
    val instructorName: InputState = InputState.NotEmptyState,
    val courseTitle: InputState = InputState.NotEmptyState,
    val googleCode: InputState = InputState.EmptyState,
    val blcCode: InputState = InputState.EmptyState,
    val courseOutline: InputState = InputState.EmptyState,
)

data class SectionEditTeacherState(
    val teacherList: List<Teacher> = emptyList(),
    val query: InputState = InputState.NotEmptyState,
)

data class SectionEditCourseState(
    val courseList: List<Course> = emptyList(),
    val query: InputState = InputState.NotEmptyState,
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

    private lateinit var allTeacher: List<Teacher>
    private val _teacherState = MutableStateFlow(SectionEditTeacherState())
    val teacherState: StateFlow<SectionEditTeacherState> get() = _teacherState

    private lateinit var allCourse: List<Course>
    private val _courseState = MutableStateFlow(SectionEditCourseState())
    val courseState: StateFlow<SectionEditCourseState> get() = _courseState

    fun loadDate(sectionId: String?, courseId: String) {
        launchInViewModelScope {
            setSideEffect { EmptyLoadingState }

            allTeacher = teacherRepository.getAllTeacher()
            _teacherState.value = _teacherState.value.copy(teacherList = allTeacher)

            allCourse = classRepository.getCourseList()
            _courseState.value = _courseState.value.copy(courseList = allCourse)

            sectionData = classRepository.getSectionData(sectionId)
            selectedInstructor = sectionData.instructor
            selectedCourse = sectionData.course

            allCourse.find { it.id == courseId }?.let { selectedCourse = it }

            withState {
                setState {
                    copy(
                        sectionName = sectionName.copy(value = sectionData.name),
                        instructorName = instructorName.copy(value = selectedInstructor.fullName),
                        courseTitle = courseTitle.copy(value = selectedCourse.courseCode),
                        googleCode = googleCode.copy(value = sectionData.googleCode),
                        blcCode = blcCode.copy(value = sectionData.blcCode),
                        courseOutline = courseOutline.copy(value = sectionData.courseOutline),
                    )
                }
            }

            searchTeacher(_teacherState.value.query.value)
            setSideEffect { TypedSideEffectState.Success(SectionEditSuccess.Loaded) }
        }
    }

    fun searchTeacher(query: String) {
        val newQuery = _teacherState.value.query.copy(value = query)
        _teacherState.value = _teacherState.value.copy(
            teacherList = allTeacher.filterByQuery(query),
            query = newQuery
        )
    }

    fun searchCourse(query: String) {
        val newQuery = _courseState.value.query.copy(value = query)
        val courseList = allCourse.filter { it.courseTitle.contains(query, ignoreCase = true) }

        _courseState.value = _courseState.value.copy(courseList = courseList, query = newQuery)
    }

    fun changeTeacher(teacher: Teacher) = withState {
        selectedInstructor = teacher.toInstructor()
        val newFacultyName = instructorName.copy(value = teacher.fullName)

        setState { copy(instructorName = newFacultyName) }
    }

    fun canChangeCourseOrName(): Boolean = sectionData.id.isEmpty()

    fun changeCourse(course: Course) = withState {
        selectedCourse = course
        val newCourseName = courseTitle.copy(value = course.courseCode)

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

        setState { copy(courseOutline = newOutline) }
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

                val alreadyCreated = classRepository.alreadySectionCreated(
                    sectionName = newSection.name,
                    courseId = newSection.course.id
                )

                if (newSection.id.isEmpty() && alreadyCreated) {
                    setSideEffect {
                        TypedSideEffectState.Fail(
                            "Someone already created ${newSection.name} for ${newSection.course.courseTitle}"
                        )
                    }
                } else {
                    classRepository.saveSection(newSection)

                    setSideEffect { TypedSideEffectState.Success(SectionEditSuccess.Saved) }
                }
            }
        }
    }


    override fun onCoroutineException(exception: Throwable) {
        setSideEffect {
            TypedSideEffectState.Fail(exception.message ?: String.Error)
        }
    }
}