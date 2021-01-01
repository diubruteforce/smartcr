package io.github.diubruteforce.smartcr.ui.event

import androidx.hilt.lifecycle.ViewModelInject
import io.github.diubruteforce.smartcr.data.repository.ExtraFeatureRepository
import io.github.diubruteforce.smartcr.model.data.Event
import io.github.diubruteforce.smartcr.model.ui.EmptyLoadingState
import io.github.diubruteforce.smartcr.model.ui.Error
import io.github.diubruteforce.smartcr.model.ui.TypedSideEffectState
import io.github.diubruteforce.smartcr.utils.base.BaseViewModel

data class EventDetailsState(
    val event: Event? = null
)

enum class EventDetailSuccess {
    Loaded, Deleted
}

class EventDetailsViewModel @ViewModelInject constructor(
    private val extraFeatureRepository: ExtraFeatureRepository
) : BaseViewModel<EventDetailsState, Any, EventDetailSuccess, String>(
    initialState = EventDetailsState()
) {
    fun loadData(eventId: String) = launchInViewModelScope {
        setSideEffect { EmptyLoadingState }

        val event = extraFeatureRepository.getEvent(eventId)

        withState { setState { copy(event = event) } }
        setSideEffect { TypedSideEffectState.Success(EventDetailSuccess.Loaded) }
    }

    fun deleteEvent() = withState {
        event?.let {
            launchInViewModelScope {
                setSideEffect { EmptyLoadingState }
                extraFeatureRepository.deleteEvent(it)
                setSideEffect { TypedSideEffectState.Success(EventDetailSuccess.Deleted) }
            }
        }
    }

    override fun onCoroutineException(exception: Throwable) {
        setSideEffect { TypedSideEffectState.Fail(exception.message ?: String.Error) }
    }
}