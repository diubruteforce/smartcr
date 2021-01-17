package io.github.diubruteforce.smartcr.ui.resource

import android.net.Uri
import androidx.hilt.lifecycle.ViewModelInject
import io.github.diubruteforce.smartcr.data.repository.ClassRepository
import io.github.diubruteforce.smartcr.data.repository.StorageRepository
import io.github.diubruteforce.smartcr.model.data.Resource
import io.github.diubruteforce.smartcr.model.data.Section
import io.github.diubruteforce.smartcr.model.ui.EmptyLoadingState
import io.github.diubruteforce.smartcr.model.ui.EmptySuccessState
import io.github.diubruteforce.smartcr.model.ui.InputState
import io.github.diubruteforce.smartcr.model.ui.TypedSideEffectState
import io.github.diubruteforce.smartcr.ui.common.ProgressType
import io.github.diubruteforce.smartcr.utils.base.StringFailSideEffectViewModel
import java.util.*

data class ResourceState(
    val resources: List<Pair<Resource, ProgressType>> = emptyList(),
    val editingResource: Resource? = null,
    val query: InputState = InputState.EmptyState,
    val title: InputState = InputState.NotEmptyState,
    val section: InputState = InputState.NotEmptyState,
    val file: InputState = InputState.NotEmptyState,
)

class ResourceViewModel @ViewModelInject constructor(
    private val classRepository: ClassRepository,
    private val storageRepository: StorageRepository,
) : StringFailSideEffectViewModel<ResourceState>(
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
            val hasDownloaded = titleList.keys.contains(resource.fileName)

            val progressType =
                if (hasDownloaded) ProgressType.View(titleList.getValue(resource.fileName))
                else ProgressType.Download

            resource to progressType
        }

        withState {
            setState {
                copy(resources = resourceMap)
            }
        }

        setSideEffect { EmptySuccessState }
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
                title = title.copy(value = resource.name, isError = false),
                section = section.copy(value = sectionName, isError = false),
                file = file.copy(value = resource.nameWithType, isError = false)
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
            isError.not()
            && editingResource != null
        ) launchInViewModelScope {
            setSideEffect { EmptyLoadingState }

            if (selectedFile != null && selectedSection != null) { // for new resource
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
            } else if (selectedSection != null) { // for update resource which section has changed
                val newResource = editingResource.copy(
                    course = selectedSection.course,
                    instructor = selectedSection.instructor,
                    sectionId = selectedSection.id,
                    sectionName = selectedSection.name,
                    name = title.value
                )

                classRepository.updateResource(newResource)
            } else if (editingResource.name != title.value) { // for update resource which name has changed
                val newResource = editingResource.copy(
                    name = title.value
                )

                classRepository.updateResource(newResource)
            }

            setState { copy(editingResource = null) }
            loadData()
        }
    }

    fun downloadFile(resource: Resource) = launchInViewModelScope {
        setSideEffect { EmptyLoadingState }
        storageRepository.download(resource)
        loadData()
    }

    fun permissionNotGranted(failMessage: String) {
        setSideEffect { TypedSideEffectState.Fail(failMessage) }
    }

    fun deleteFile(resource: Resource) = withState {
        launchInViewModelScope {
            setSideEffect { EmptyLoadingState }

            val newResource = resource.copy(isActive = false)

            classRepository.updateResource(newResource)

            loadData()
        }
    }

    fun setFileNotUpLoadable() {
        setSideEffect { TypedSideEffectState.Fail("File size is more than 5 MB. Please choose a file less than 5 MB") }
    }
}