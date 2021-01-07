package io.github.diubruteforce.smartcr.ui.resource

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.SavedStateHandle
import io.github.diubruteforce.smartcr.data.repository.ClassRepository
import io.github.diubruteforce.smartcr.model.data.Section
import io.github.diubruteforce.smartcr.model.ui.Error
import io.github.diubruteforce.smartcr.model.ui.TypedSideEffectState
import io.github.diubruteforce.smartcr.utils.base.BaseViewModel

data class ResourceState(
    val joinedSections: List<Section> = emptyList()
)

enum class ResourceSuccess {
    Loaded, Filtered, Saved, Deleted
}

class ResourceViewModel @ViewModelInject constructor(
    private val classRepository: ClassRepository,
    @Assisted private val savedStateHandle: SavedStateHandle
) : BaseViewModel<ResourceState, Any, ResourceSuccess, String>(
    initialState = ResourceState()
) {
    override fun onCoroutineException(exception: Throwable) {
        setSideEffect { TypedSideEffectState.Fail(exception.message ?: String.Error) }
    }
}