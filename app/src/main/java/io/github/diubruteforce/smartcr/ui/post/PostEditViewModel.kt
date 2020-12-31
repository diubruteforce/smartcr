package io.github.diubruteforce.smartcr.ui.post

import androidx.hilt.lifecycle.ViewModelInject
import io.github.diubruteforce.smartcr.data.repository.ClassRepository
import io.github.diubruteforce.smartcr.model.data.*
import io.github.diubruteforce.smartcr.model.ui.EmptyLoadingState
import io.github.diubruteforce.smartcr.model.ui.Error
import io.github.diubruteforce.smartcr.model.ui.InputState
import io.github.diubruteforce.smartcr.model.ui.TypedSideEffectState
import io.github.diubruteforce.smartcr.utils.base.BaseViewModel
import io.github.diubruteforce.smartcr.utils.extension.toDateTimeMillis

data class PostEditState(
    val number: InputState = InputState.NotEmptyState,
    val date: InputState = InputState.NotEmptyState,
    val time: InputState = InputState.NotEmptyState,
    val details: InputState = InputState.EmptyState,
    val place: InputState = InputState.NotEmptyState,
    val groupType: InputState = InputState.NotEmptyState,
    val maxMember: InputState = InputState.NotEmptyState,
    val syllabus: InputState = InputState.NotEmptyState,
    val section: InputState = InputState.NotEmptyState,
    val post: Post = Quiz()
)

enum class PostEditSuccess {
    Loaded, Saved
}

class PostEditViewModel @ViewModelInject constructor(
    private val classRepository: ClassRepository
) : BaseViewModel<PostEditState, Any, PostEditSuccess, String>(
    initialState = PostEditState()
) {
    private var selectedSection: Section? = null
    private var _joinedSections: List<Section> = emptyList()
    val joinedSections get() = _joinedSections

    fun loadData(postType: PostType, postId: String?) = launchInViewModelScope {
        setSideEffect { EmptyLoadingState }

        val post = classRepository.getPost(postType, postId)
        _joinedSections = classRepository.getJoinedSectionList()
        selectedSection = _joinedSections.find { it.id == post.sectionId }

        val sectionValue = if (post.courseCode.isEmpty()) ""
        else "${post.courseCode} (${post.sectionName})"

        withState {
            val newNumber = number.copy(value = post.number)
            val newDate = date.copy(value = post.date)
            val newTime = time.copy(value = post.time)
            val newDetails = details.copy(value = post.details)
            val newPlace = place.copy(value = post.place)
            val newSection = section.copy(value = sectionValue)

            val groupTypeValue = when (post) {
                is Quiz -> GroupType.Single.name
                is Assignment -> post.groupType
                is Presentation -> post.groupType
                is Project -> post.groupType
            }
            val newGroupType = groupType.copy(value = groupTypeValue)

            val newMaxMemberValue = when (post) {
                is Quiz -> 1
                is Assignment -> post.maxMember
                is Presentation -> post.maxMember
                is Project -> post.maxMember
            }

            val newMaxMember = maxMember.copy(value = newMaxMemberValue.toString())

            val newSyllabus = syllabus.copy(value = if (post is Quiz) post.syllabus else "")

            setState {
                copy(
                    number = newNumber,
                    date = newDate,
                    time = newTime,
                    details = newDetails,
                    place = newPlace,
                    groupType = newGroupType,
                    maxMember = newMaxMember,
                    syllabus = newSyllabus,
                    section = newSection,
                    post = post
                )
            }

            setSideEffect { TypedSideEffectState.Success(PostEditSuccess.Loaded) }
        }
    }

    fun changeNumber(newNumber: String) = withState {
        setState { copy(number = number.copy(value = newNumber)) }
    }

    fun changeDate(newDate: String) = withState {
        setState { copy(date = date.copy(value = newDate)) }
    }

    fun changeTime(newTime: String) = withState {
        setState { copy(time = time.copy(value = newTime)) }
    }

    fun changeDetails(newDetails: String) = withState {
        setState { copy(details = details.copy(value = newDetails)) }
    }

    fun changePlace(newPlace: String) = withState {
        setState { copy(place = place.copy(value = newPlace)) }
    }

    fun changeGroupType(newGroupType: GroupType) = withState {
        setState {
            copy(
                groupType = groupType.copy(value = newGroupType.name),
                maxMember = maxMember.copy(value = "1")
            )
        }
    }

    fun changeMaxMember(newMaxMember: String) = withState {
        setState { copy(maxMember = maxMember.copy(value = newMaxMember)) }
    }

    fun changeSyllabus(newSyllabus: String) = withState {
        setState { copy(syllabus = syllabus.copy(value = newSyllabus)) }
    }

    fun changeSection(newSection: Section) = withState {
        selectedSection = newSection

        setState {
            copy(
                section = section.copy(
                    value = "${newSection.course.courseCode} (${newSection.name})"
                )
            )
        }
    }

    fun savePost() = withState {
        val newNumber = number.validate()
        val newDate = date.validate()
        val newTime = time.validate()
        val newDetails = details.validate()
        val newPlace = place.validate()
        val newGroupType = groupType.validate()
        val newMaxMember = maxMember.validate()
        val newSyllabus = syllabus.validate()
        val newSection = section.validate()

        val selectedSection = selectedSection

        var isError = newNumber.isError ||
                newDate.isError ||
                newTime.isError ||
                newDetails.isError ||
                newPlace.isError ||
                newSection.isError ||
                selectedSection == null

        isError = if (post is Quiz) isError || newSyllabus.isError else isError

        setState {
            copy(
                number = newNumber,
                date = newDate,
                time = newTime,
                details = newDetails,
                place = newPlace,
                groupType = newGroupType,
                maxMember = newMaxMember,
                syllabus = newSyllabus,
                section = newSection
            )
        }

        if (isError.not() && selectedSection != null) launchInViewModelScope {
            setSideEffect { EmptyLoadingState }

            val newPost = when (post) {
                is Quiz -> post.copy(
                    number = newNumber.value,
                    date = newDate.value,
                    time = newTime.value,
                    dateTimeMillis = "${newDate.value} 11:59 PM".toDateTimeMillis(),
                    details = newDetails.value,
                    place = newPlace.value,
                    syllabus = newSyllabus.value,
                    sectionId = selectedSection.id,
                    sectionName = selectedSection.name,
                    courseName = selectedSection.course.courseTitle,
                    courseCode = selectedSection.course.courseCode
                )
                is Assignment -> post.copy(
                    number = newNumber.value,
                    date = newDate.value,
                    time = newTime.value,
                    dateTimeMillis = "${newDate.value} 11:59 PM".toDateTimeMillis(),
                    details = newDetails.value,
                    place = newPlace.value,
                    groupType = newGroupType.value,
                    maxMember = newMaxMember.value.toInt(),
                    sectionId = selectedSection.id,
                    sectionName = selectedSection.name,
                    courseName = selectedSection.course.courseTitle,
                    courseCode = selectedSection.course.courseCode
                )
                is Presentation -> post.copy(
                    number = newNumber.value,
                    date = newDate.value,
                    time = newTime.value,
                    dateTimeMillis = "${newDate.value} 11:59 PM".toDateTimeMillis(),
                    details = newDetails.value,
                    place = newPlace.value,
                    groupType = newGroupType.value,
                    maxMember = newMaxMember.value.toInt(),
                    sectionId = selectedSection.id,
                    sectionName = selectedSection.name,
                    courseName = selectedSection.course.courseTitle,
                    courseCode = selectedSection.course.courseCode
                )
                is Project -> post.copy(
                    number = newNumber.value,
                    date = newDate.value,
                    time = newTime.value,
                    dateTimeMillis = "${newDate.value} 11:59 PM".toDateTimeMillis(),
                    details = newDetails.value,
                    place = newPlace.value,
                    groupType = newGroupType.value,
                    maxMember = newMaxMember.value.toInt(),
                    sectionId = selectedSection.id,
                    sectionName = selectedSection.name,
                    courseName = selectedSection.course.courseTitle,
                    courseCode = selectedSection.course.courseCode
                )
            }

            classRepository.savePost(newPost)

            setSideEffect { TypedSideEffectState.Success(PostEditSuccess.Saved) }
        }
        else setSideEffect {
            TypedSideEffectState.Fail("Some of inputs are invalid. Please check and try again.")
        }
    }

    override fun onCoroutineException(exception: Throwable) {
        setSideEffect { TypedSideEffectState.Fail(exception.message ?: String.Error) }
    }
}