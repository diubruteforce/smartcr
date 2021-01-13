package io.github.diubruteforce.smartcr.ui.resource

import android.net.Uri
import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.SavedStateHandle
import io.github.diubruteforce.smartcr.data.repository.ClassRepository
import io.github.diubruteforce.smartcr.data.repository.StorageRepository
import io.github.diubruteforce.smartcr.model.data.Resource
import io.github.diubruteforce.smartcr.model.data.Section
import io.github.diubruteforce.smartcr.model.ui.EmptyLoadingState
import io.github.diubruteforce.smartcr.model.ui.Error
import io.github.diubruteforce.smartcr.model.ui.InputState
import io.github.diubruteforce.smartcr.model.ui.TypedSideEffectState
import io.github.diubruteforce.smartcr.ui.common.ProgressType
import io.github.diubruteforce.smartcr.utils.base.BaseViewModel
import java.util.*

data class ResourceState(
    val resources: List<Pair<Resource, ProgressType>> = emptyList(),
    val editingResource: Resource? = null,
    val query: InputState = InputState.EmptyState,
    val title: InputState = InputState.NotEmptyState,
    val section: InputState = InputState.NotEmptyState,
    val file: InputState = InputState.NotEmptyState,
)

enum class ResourceSuccess {
    Loaded, Filtered, Saved, Deleted
}

class ResourceViewModel @ViewModelInject constructor(
    private val classRepository: ClassRepository,
    private val storageRepository: StorageRepository,
    @Assisted private val savedStateHandle: SavedStateHandle
) : BaseViewModel<ResourceState, Any, ResourceSuccess, String>(
    initialState = ResourceState()
) {
    private var selectedSection: Section? = null
    private var selectedFile: Uri? = null

    private var _joinedSections: List<Section> = emptyList()
    val joinedSections get() = _joinedSections

    private lateinit var resourceMap: List<Pair<Resource, ProgressType>>

    init {
        setSideEffect { EmptyLoadingState }
        loadData()
    }

    fun loadData() = launchInViewModelScope {
        _joinedSections = classRepository.getJoinedSectionList()

        val titleList = storageRepository.listTitles()
        resourceMap = classRepository.getResources().map { resource ->
            val hasDownloaded = titleList.contains(resource.fileName)

            val progressType = if (hasDownloaded) ProgressType.View(Uri.parse(resource.name))
            else ProgressType.Download

            resource to progressType
        }

        withState {
            setState {
                copy(resources = resourceMap)
            }
        }

        setSideEffect { TypedSideEffectState.Success(ResourceSuccess.Loaded) }
    }

    fun search(newQuery: String) = withState {
        val newResourceMap = resourceMap.filter {
            it.first.fileName.contains(newQuery)
                    || it.first.course.courseTitle.contains(newQuery)
                    || it.first.instructor.fullName.contains(newQuery)
                    || it.first.instructor.initial.contains(newQuery)
                    || it.first.instructor.department.contains(newQuery)
                    || it.first.instructor.departmentCode.contains(newQuery)
                    || it.first.uploadedBy.contains(newQuery)
                    || it.first.updaterEmail.contains(newQuery)
        }

        setState { copy(resources = newResourceMap, query = query.copy(value = newQuery)) }
    }

    fun startEditing(resource: Resource) = withState {
        val sectionName = if (resource.course.id.isEmpty()) ""
        else "${resource.course.courseCode} (${resource.sectionName})"

        setState {
            copy(
                editingResource = resource,
                title = title.copy(value = resource.name),
                section = section.copy(value = sectionName),
                file = file.copy(value = resource.path)
            )
        }
    }

    fun cancelEditing() = withState {
        setState { copy(editingResource = null) }
    }

    fun onTitleChange(newTitle: String) = withState {
        setState { copy(title = title.copy(value = newTitle)) }
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

    fun canChangeFile(): Boolean {
        val canChange = state.value.editingResource?.path?.isEmpty() ?: false

        if (canChange.not()) setSideEffect {
            TypedSideEffectState.Fail("You can't change file for existing resource")
        }

        return canChange
    }

    fun changeFile(newFile: Uri, fileName: String) = withState {
        selectedFile = newFile

        setState { copy(file = file.copy(value = fileName.take(20))) }
    }

    fun uploadFile() = withState {
        val newTitle = title.validate()
        val newSection = section.validate()
        val newFile = file.validate()

        setState {
            copy(
                title = newTitle,
                section = newSection,
                file = newFile
            )
        }

        val isError = newTitle.isError || newSection.isError || newFile.isError
        val selectedFile = selectedFile
        val selectedSection = selectedSection

        if (
            isError.not() && selectedFile != null
            && selectedSection != null
            && editingResource != null
        ) launchInViewModelScope {
            setSideEffect { EmptyLoadingState }

            val newResource = editingResource.copy(
                course = selectedSection.course,
                instructor = selectedSection.instructor,
                sectionId = selectedSection.id,
                sectionName = selectedSection.name,
                path = if (editingResource.id.isEmpty()) UUID.randomUUID().toString()
                else editingResource.id, //TODO: change this logic
                name = title.value
            )

            classRepository.saveResource(newResource, selectedFile)

            setState { copy(editingResource = null) }
            loadData()
        }
    }

    fun downloadFile(resource: Resource) = launchInViewModelScope {
        setSideEffect { EmptyLoadingState }
        storageRepository.download(resource)
        loadData()
    }

    override fun onCoroutineException(exception: Throwable) {
        setSideEffect { TypedSideEffectState.Fail(exception.message ?: String.Error) }
    }
}