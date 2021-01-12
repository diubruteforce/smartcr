package io.github.diubruteforce.smartcr.ui.resource

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.SavedStateHandle
import io.github.diubruteforce.smartcr.data.repository.ClassRepository
import io.github.diubruteforce.smartcr.model.data.Resource
import io.github.diubruteforce.smartcr.model.data.Section
import io.github.diubruteforce.smartcr.model.ui.Error
import io.github.diubruteforce.smartcr.model.ui.InputState
import io.github.diubruteforce.smartcr.model.ui.TypedSideEffectState
import io.github.diubruteforce.smartcr.ui.common.ProgressType
import io.github.diubruteforce.smartcr.utils.base.BaseViewModel

data class ResourceState(
    val resources: List<Pair<Resource, ProgressType>> = emptyList(),
    val joinedSections: List<Section> = emptyList(),
    val editingResource: Resource? = null,
    val title: InputState = InputState.NotEmptyState,
    val section: InputState = InputState.NotEmptyState,
    val file: InputState = InputState.NotEmptyState,
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
    private var downloadedResources: List<Resource> = emptyList()

    fun loadData() = launchInViewModelScope {

    }

    override fun onCoroutineException(exception: Throwable) {
        setSideEffect { TypedSideEffectState.Fail(exception.message ?: String.Error) }
    }
}