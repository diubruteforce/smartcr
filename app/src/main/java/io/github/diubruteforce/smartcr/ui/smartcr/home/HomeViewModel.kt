package io.github.diubruteforce.smartcr.ui.smartcr.home

import androidx.hilt.lifecycle.ViewModelInject
import io.github.diubruteforce.smartcr.data.repository.ClassRepository
import io.github.diubruteforce.smartcr.model.data.PostType
import io.github.diubruteforce.smartcr.model.ui.EmptyLoadingState
import io.github.diubruteforce.smartcr.model.ui.EmptySuccessState
import io.github.diubruteforce.smartcr.model.ui.PostCardState
import io.github.diubruteforce.smartcr.utils.base.StringFailSideEffectViewModel
import io.github.diubruteforce.smartcr.utils.extension.toDateString
import io.github.diubruteforce.smartcr.utils.extension.toDateTimeMillis
import io.github.diubruteforce.smartcr.utils.extension.toDay
import java.util.*

data class HomeState(
    val posts: List<PostCardState> = emptyList(),
    val hasJoinedSection: Boolean = false,
    val currentDate: String = ""
)

class HomeViewModel @ViewModelInject constructor(
    private val classRepository: ClassRepository
) : StringFailSideEffectViewModel<HomeState>(
    initialState = HomeState()
) {
    private var currentDateTime = Calendar.getInstance(Locale.getDefault())

    init {
        setSideEffect { EmptyLoadingState }
    }

    fun loadData() = launchInViewModelScope {
        val currentDay = currentDateTime.toDay()
        val currentDate = currentDateTime.toDateString()

        val routineList = classRepository.getStudentRoutineList()
        val postList = classRepository.getTodayPostList(currentDate)


        val postStateList = routineList
            .filter { it.day.equals(currentDay, true) }
            .map {
                PostCardState(
                    type = PostType.Routine,
                    title = "${it.courseCode} (Class)",
                    firstRow = "Time: ${it.startTime} - ${it.endTime}",
                    dateTimeMillis = "$currentDate ${it.startTime}".toDateTimeMillis(),
                    secondRow = "Room: ${it.room}",
                    sectionId = it.sectionId,
                    postId = it.id
                )
            }
            .toMutableList()

        postList.mapTo(postStateList) {
            PostCardState(
                type = PostType.valueOf(it.postType),
                title = "${it.courseCode} (${it.number})",
                firstRow = "Time: ${it.time}",
                dateTimeMillis = it.dateTimeMillis,
                secondRow = "Place: ${it.place}",
                sectionId = it.sectionId,
                postId = it.id
            )
        }

        val hasJoinedInAnySection = classRepository.getUserProfile().joinedSection.isNotEmpty()

        postStateList.sortBy { it.dateTimeMillis }

        withState {
            setState {
                copy(
                    posts = postStateList,
                    currentDate = currentDate,
                    hasJoinedSection = hasJoinedInAnySection
                )
            }
        }

        setSideEffect { EmptySuccessState }
    }

    fun nextDay() {
        currentDateTime.add(Calendar.DATE, 1)
        setSideEffect { EmptyLoadingState }
        loadData()
    }

    fun previousDay() {
        currentDateTime.add(Calendar.DATE, -1)
        setSideEffect { EmptyLoadingState }
        loadData()
    }
}