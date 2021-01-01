package io.github.diubruteforce.smartcr.ui.feesschedule

import androidx.hilt.lifecycle.ViewModelInject
import io.github.diubruteforce.smartcr.data.repository.ExtraFeatureRepository
import io.github.diubruteforce.smartcr.model.data.FeesReason
import io.github.diubruteforce.smartcr.model.data.FeesSchedule
import io.github.diubruteforce.smartcr.model.ui.EmptyLoadingState
import io.github.diubruteforce.smartcr.model.ui.EmptySuccessState
import io.github.diubruteforce.smartcr.model.ui.InputState
import io.github.diubruteforce.smartcr.utils.base.StringFailSideEffectViewModel
import io.github.diubruteforce.smartcr.utils.extension.toDateTimeMillis
import java.util.*

data class FeesScheduleState(
    val feesSchedules: List<FeesSchedule> = emptyList(),
    val editingFeesSchedule: FeesSchedule? = null,
    val batchCode: InputState = InputState.NotEmptyState,
    val feesFor: InputState = InputState.NotEmptyState,
    val lastDate: InputState = InputState.NotEmptyState
)

class FeesScheduleViewModel @ViewModelInject constructor(
    private val extraFeatureRepository: ExtraFeatureRepository
) : StringFailSideEffectViewModel<FeesScheduleState>(
    initialState = FeesScheduleState()
) {
    private var currentDateTime = Calendar.getInstance(Locale.getDefault())

    fun loadData() = launchInViewModelScope {
        setSideEffect { EmptyLoadingState }

        val dateTimeMillis = currentDateTime.timeInMillis

        val feesSchedules = extraFeatureRepository.getFees(dateTimeMillis)

        withState {
            setState {
                copy(feesSchedules = feesSchedules)
            }
        }

        setSideEffect { EmptySuccessState }
    }

    fun startEditing(feesSchedule: FeesSchedule) = withState {
        setState {
            copy(
                editingFeesSchedule = feesSchedule,
                batchCode = batchCode.copy(value = feesSchedule.batchCode),
                lastDate = lastDate.copy(value = feesSchedule.lastDate),
                feesFor = feesFor.copy(value = feesSchedule.feesFor)
            )
        }
    }

    fun clearEditing() = withState {
        setState { copy(editingFeesSchedule = null) }
    }

    fun changeBatchCode(code: String) = withState {
        setState { copy(batchCode = batchCode.copy(value = code)) }
    }

    fun changeFeesFor(feesReason: FeesReason) = withState {
        setState { copy(feesFor = feesFor.copy(value = feesReason.title)) }
    }

    fun changeLastDate(date: String) = withState {
        setState { copy(lastDate = lastDate.copy(value = date)) }
    }

    fun saveFeesSchedule() = withState {
        val newBatchCode = batchCode.validate()
        val newLastDate = lastDate.validate()
        val newFeesFor = feesFor.validate()

        setState {
            copy(
                batchCode = newBatchCode,
                lastDate = newLastDate,
                feesFor = newFeesFor
            )
        }

        val isError = newBatchCode.isError || newLastDate.isError || newFeesFor.isError

        if (isError.not() && editingFeesSchedule != null) launchInViewModelScope {
            setSideEffect { EmptyLoadingState }

            val newFeesSchedule = editingFeesSchedule.copy(
                batchCode = newBatchCode.value,
                lastDate = newLastDate.value,
                feesFor = newFeesFor.value,
                dateTimeMillis = "${newLastDate.value} 11:59 PM".toDateTimeMillis()
            )

            extraFeatureRepository.saveFeesSchedule(newFeesSchedule)

            setState { copy(editingFeesSchedule = null) }
            loadData()
        }
    }

    fun deleteFeesSchedule(feesSchedule: FeesSchedule) = launchInViewModelScope {
        setSideEffect { EmptyLoadingState }

        extraFeatureRepository.saveFeesSchedule(feesSchedule.copy(isActive = false))

        loadData()
    }
}