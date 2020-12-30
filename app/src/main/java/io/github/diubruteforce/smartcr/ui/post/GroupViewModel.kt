package io.github.diubruteforce.smartcr.ui.post

import androidx.hilt.lifecycle.ViewModelInject
import io.github.diubruteforce.smartcr.data.repository.ClassRepository
import io.github.diubruteforce.smartcr.model.data.Group
import io.github.diubruteforce.smartcr.model.data.MemberStudent
import io.github.diubruteforce.smartcr.model.ui.EmptyLoadingState
import io.github.diubruteforce.smartcr.model.ui.Error
import io.github.diubruteforce.smartcr.model.ui.InputState
import io.github.diubruteforce.smartcr.model.ui.TypedSideEffectState
import io.github.diubruteforce.smartcr.utils.base.BaseViewModel
import kotlinx.coroutines.async

data class GroupState(
    val groups: List<Group> = emptyList(),
    val groupMembers: Map<String, List<MemberStudent>> = emptyMap(),
    val joinedGroupId: String? = null,

    val editingGroup: Group? = null,
    val groupName: InputState = InputState.NotEmptyState,
    val groupDetails: InputState = InputState.EmptyState
)

enum class GroupSuccess {
    Loaded, Joined, Left, GroupSaved
}

class GroupViewModel @ViewModelInject constructor(
    private val classRepository: ClassRepository
) : BaseViewModel<GroupState, Any, GroupSuccess, String>(
    initialState = GroupState()
) {
    private lateinit var savedPostId: String
    private lateinit var savedSectionId: String

    fun loadData(postId: String, sectionId: String) = launchInViewModelScope {
        setSideEffect { EmptyLoadingState }

        savedPostId = postId
        savedSectionId = sectionId

        val memberList = async { classRepository.getMemberStudentList(sectionId) }
        val groupList = async { classRepository.getGroupList(postId) }

        val members = memberList.await()
        val groups = groupList.await()

        val groupMembers = members
            .flatMap { member -> member.joinedGroups.map { it to member } }
            .groupBy({ it.first }, { it.second })


        val userProfile = classRepository.getUserProfile()
        val userMember = members.find { it.studentId == userProfile.id }!!

        val joinedGroupId = groups.find { userMember.joinedGroups.contains(it.id) }?.id

        withState {
            setState {
                copy(
                    groups = groups,
                    groupMembers = groupMembers,
                    joinedGroupId = joinedGroupId
                )
            }
        }

        setSideEffect { TypedSideEffectState.Success(GroupSuccess.Loaded) }
    }


    fun startEditing(group: Group) = withState {
        setState {
            copy(
                groupName = groupName.copy(value = group.name),
                groupDetails = groupDetails.copy(value = group.detail),
                editingGroup = group
            )
        }
    }

    fun changeGroupName(newName: String) = withState {
        setState { copy(groupName = groupName.copy(value = newName)) }
    }

    fun changeGroupDetails(newDetails: String) = withState {
        setState { copy(groupDetails = groupDetails.copy(value = newDetails)) }
    }

    fun editGroup() = withState {
        val newGroupName = groupName.validate()
        val newGroupDetail = groupDetails.validate()

        val isError = newGroupName.isError || newGroupDetail.isError

        setState {
            copy(groupName = groupName, groupDetails = groupDetails)
        }

        if (isError.not() && editingGroup != null) launchInViewModelScope {
            setSideEffect { EmptyLoadingState }

            val newGroup = editingGroup.copy(
                name = newGroupName.value,
                detail = newGroupDetail.value
            )

            classRepository.editGroup(savedPostId, newGroup)

            setSideEffect { TypedSideEffectState.Success(GroupSuccess.GroupSaved) }
        }
    }

    override fun onCoroutineException(exception: Throwable) {
        setSideEffect { TypedSideEffectState.Fail(exception.message ?: String.Error) }
    }
}