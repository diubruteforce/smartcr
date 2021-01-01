package io.github.diubruteforce.smartcr.ui.event

import androidx.hilt.lifecycle.ViewModelInject
import io.github.diubruteforce.smartcr.data.repository.ExtraFeatureRepository
import io.github.diubruteforce.smartcr.model.data.Event
import io.github.diubruteforce.smartcr.model.data.EventType
import io.github.diubruteforce.smartcr.model.ui.EmptyLoadingState
import io.github.diubruteforce.smartcr.model.ui.Error
import io.github.diubruteforce.smartcr.model.ui.InputState
import io.github.diubruteforce.smartcr.model.ui.TypedSideEffectState
import io.github.diubruteforce.smartcr.utils.base.BaseViewModel

data class EventEditState(
    val title: InputState = InputState.NotEmptyState,
    val type: InputState = InputState.NotEmptyState,
    val date: InputState = InputState.NotEmptyState,
    val time: InputState = InputState.NotEmptyState,
    val place: InputState = InputState.NotEmptyState,
    val details: InputState = InputState.EmptyState,
)

enum class EventEditSuccess {
    Loaded, Saved
}

class EventEditViewModel @ViewModelInject constructor(
    private val extraFeatureRepository: ExtraFeatureRepository
) : BaseViewModel<EventEditState, Any, EventEditSuccess, String>(
    initialState = EventEditState()
) {
    lateinit var savedEvent: Event

    fun loadData(eventId: String?) = launchInViewModelScope {
        setSideEffect { EmptyLoadingState }

        savedEvent = extraFeatureRepository.getEvent(eventId)

        withState {
            setState {
                copy(
                    title = title.copy(value = savedEvent.title),
                    type = type.copy(value = savedEvent.type),
                    date = date.copy(value = savedEvent.date),
                    time = time.copy(value = savedEvent.time),
                    place = place.copy(value = savedEvent.place),
                    details = details.copy(value = savedEvent.details)
                )
            }
        }

        setSideEffect { TypedSideEffectState.Success(EventEditSuccess.Loaded) }
    }

    fun changeTitle(newValue: String) = withState {
        setState { copy(title = title.copy(value = newValue)) }
    }

    fun changeType(newValue: EventType) = withState {
        setState { copy(type = type.copy(value = newValue.name)) }
    }

    fun changeDate(newValue: String) = withState {
        setState { copy(date = date.copy(value = newValue)) }
    }

    fun changeTime(newValue: String) = withState {
        setState { copy(time = time.copy(value = newValue)) }
    }

    fun changePlace(newValue: String) = withState {
        setState { copy(place = place.copy(value = newValue)) }
    }

    fun changeDetails(newValue: String) = withState {
        setState { copy(details = details.copy(value = newValue)) }
    }

    fun saveEvent() = withState {
        val newTitle = title.validate()
        val newType = type.validate()
        val newDate = date.validate()
        val newTime = time.validate()
        val newPlace = place.validate()
        val newDetails = details.validate()

        setState {
            copy(
                title = newTitle,
                type = newType,
                date = newDate,
                time = newTime,
                place = newPlace,
                details = newDetails
            )
        }

        val isError = newTitle.isError ||
                newType.isError ||
                newDate.isError ||
                newTime.isError ||
                newPlace.isError ||
                newDetails.isError

        if (isError.not()) launchInViewModelScope {
            setSideEffect { EmptyLoadingState }

            val newEvent = savedEvent.copy(
                title = newTitle.value,
                type = newType.value,
                date = newDate.value,
                time = newTime.value,
                place = newPlace.value,
                details = newDetails.value
            )

            extraFeatureRepository.saveEvent(newEvent)

            setSideEffect { TypedSideEffectState.Success(EventEditSuccess.Saved) }
        }
    }

    override fun onCoroutineException(exception: Throwable) {
        setSideEffect { TypedSideEffectState.Fail(exception.message ?: String.Error) }
    }
}