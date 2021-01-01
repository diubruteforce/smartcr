package io.github.diubruteforce.smartcr.ui.event

import androidx.hilt.lifecycle.ViewModelInject
import io.github.diubruteforce.smartcr.data.repository.ExtraFeatureRepository
import io.github.diubruteforce.smartcr.model.data.Event
import io.github.diubruteforce.smartcr.model.ui.EmptyLoadingState
import io.github.diubruteforce.smartcr.model.ui.EmptySuccessState
import io.github.diubruteforce.smartcr.utils.base.StringFailSideEffectViewModel
import java.util.*

data class EventListState(
    val events: List<Event> = emptyList()
)

class EventListViewModel @ViewModelInject constructor(
    private val extraFeatureRepository: ExtraFeatureRepository
) : StringFailSideEffectViewModel<EventListState>(
    initialState = EventListState()
) {
    private val currentDate = Calendar.getInstance(Locale.getDefault())

    fun loadData() = launchInViewModelScope {
        setSideEffect { EmptyLoadingState }

        val eventList = extraFeatureRepository.getEventList(currentDate.timeInMillis)

        withState { setState { copy(events = eventList) } }
        setSideEffect { EmptySuccessState }
    }
}