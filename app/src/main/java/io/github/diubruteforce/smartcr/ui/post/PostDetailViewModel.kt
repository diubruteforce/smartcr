package io.github.diubruteforce.smartcr.ui.post

import androidx.hilt.lifecycle.ViewModelInject
import io.github.diubruteforce.smartcr.data.repository.ClassRepository
import io.github.diubruteforce.smartcr.model.data.Group
import io.github.diubruteforce.smartcr.model.data.Post
import io.github.diubruteforce.smartcr.model.data.PostType
import io.github.diubruteforce.smartcr.model.ui.EmptyLoadingState
import io.github.diubruteforce.smartcr.model.ui.Error
import io.github.diubruteforce.smartcr.model.ui.TypedSideEffectState
import io.github.diubruteforce.smartcr.utils.base.BaseViewModel

data class PostDetailState(
    val post: Post? = null,
    val joinedGroup: Group? = null
)

enum class PostDetailSuccess {
    Loaded, Deleted
}

class PostDetailViewModel @ViewModelInject constructor(
    private val classRepository: ClassRepository
) : BaseViewModel<PostDetailState, Any, PostDetailSuccess, String>(
    initialState = PostDetailState()
) {
    fun loadData(postType: PostType, postId: String) = launchInViewModelScope {
        setSideEffect { EmptyLoadingState }

        val post = classRepository.getPost(postType, postId)
        val joinedGroup = classRepository.getJoinedGroup(postId = postId)

        withState {
            setState {
                copy(
                    post = post,
                    joinedGroup = joinedGroup
                )
            }
        }

        setSideEffect { TypedSideEffectState.Success(PostDetailSuccess.Loaded) }
    }

    fun deletePost() = launchInViewModelScope {
        setSideEffect { EmptyLoadingState }

        state.value.post?.let {
            classRepository.deletePost(it)
        }

        setSideEffect { TypedSideEffectState.Success(PostDetailSuccess.Deleted) }
    }

    override fun onCoroutineException(exception: Throwable) {
        setSideEffect { TypedSideEffectState.Fail(exception.message ?: String.Error) }
    }
}