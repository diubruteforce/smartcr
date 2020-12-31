package io.github.diubruteforce.smartcr.ui.todo

import androidx.hilt.lifecycle.ViewModelInject
import io.github.diubruteforce.smartcr.data.repository.ClassRepository
import io.github.diubruteforce.smartcr.model.data.PostType
import io.github.diubruteforce.smartcr.model.ui.EmptyLoadingState
import io.github.diubruteforce.smartcr.model.ui.EmptySuccessState
import io.github.diubruteforce.smartcr.model.ui.PostCardState
import io.github.diubruteforce.smartcr.utils.base.StringFailSideEffectViewModel
import java.util.*

data class ToDoState(
    val posts: List<PostCardState> = emptyList()
)

class ToDoViewModel @ViewModelInject constructor(
    private val classRepository: ClassRepository
) : StringFailSideEffectViewModel<ToDoState>(
    initialState = ToDoState()
) {
    private var currentDateTime = Calendar.getInstance(Locale.getDefault())

    init {
        setSideEffect { EmptyLoadingState }
    }

    fun loadData() = launchInViewModelScope {
        val currentDateMillis = currentDateTime.timeInMillis

        val posts = classRepository.getTodayTodoList(currentDateMillis)

        val postCards = posts.map {
            PostCardState(
                type = PostType.valueOf(it.postType),
                title = "${it.courseCode} (${it.number})",
                firstRow = "Time: ${it.date} at ${it.time}",
                dateTimeMillis = it.dateTimeMillis,
                secondRow = "Place: ${it.place}",
                sectionId = it.sectionId,
                postId = it.id
            )
        }

        withState { setState { copy(posts = postCards) } }
        setSideEffect { EmptySuccessState }
    }
}