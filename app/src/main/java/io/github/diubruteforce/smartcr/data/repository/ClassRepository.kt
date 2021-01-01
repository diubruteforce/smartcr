package io.github.diubruteforce.smartcr.data.repository

import com.google.firebase.Timestamp
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import io.github.diubruteforce.smartcr.model.data.*
import io.github.diubruteforce.smartcr.utils.extension.toDateString
import io.github.diubruteforce.smartcr.utils.extension.whereActiveData
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ClassRepository @Inject constructor(
    val profileRepository: ProfileRepository
) {
    private val db by lazy { Firebase.firestore }
    val departmentPath = "department"
    private val coursePath = "course"
    val semesterPath = "semester"
    private val sectionPath = "section"
    private val studentPath = "student"
    val historyPath = "history"
    private val routinePath = "routine"
    private val postPath = "post"
    private val groupPath = "group"

    private var _semesterId: String? = null
    private var _userProfile: Student? = null

    suspend fun getAllDepartment(): List<Department> {
        val result = db.collection(departmentPath).get().await()

        return result.documents
            .mapNotNull {
                it.toObject(Department::class.java)?.copy(id = it.id)
            }
            .sortedBy { it.name }
    }

    suspend fun getUserProfile(force: Boolean = false): Student {
        if (force.not() && _userProfile != null) return _userProfile!!

        _userProfile = profileRepository.getUserProfile()

        return _userProfile!!
    }

    suspend fun getSemesterId(): String {
        if (_semesterId != null) return _semesterId!!

        _semesterId = db.collection(departmentPath)
            .document(getUserProfile().departmentId)
            .collection(semesterPath)
            .orderBy("time", Query.Direction.DESCENDING)
            .limit(1)
            .get()
            .await()
            .first()
            .id

        return _semesterId!!
    }

    suspend fun getCourseList(): List<Course> {
        return db.collection(departmentPath)
            .document(getUserProfile().departmentId)
            .collection(coursePath)
            .get()
            .await()
            .map { it.toObject<Course>().copy(id = it.id) }
            .sortedBy { it.courseTitle }
    }

    suspend fun getCourse(courseId: String): Course {
        val response = db.collection(departmentPath)
            .document(getUserProfile().departmentId)
            .collection(coursePath)
            .document(courseId)
            .get()
            .await()

        return response.toObject<Course>()?.copy(id = response.id)!!
    }

    private suspend fun getSectionCollectionPath() =
        db.collection(departmentPath)
            .document(getUserProfile().departmentId)
            .collection(semesterPath)
            .document(getSemesterId())
            .collection(sectionPath)

    private suspend fun getRoutineCollectionPath() =
        db.collection(departmentPath)
            .document(getUserProfile().departmentId)
            .collection(semesterPath)
            .document(getSemesterId())
            .collection(routinePath)

    private suspend fun getPostCollectionPath() =
        db.collection(departmentPath)
            .document(getUserProfile().departmentId)
            .collection(semesterPath)
            .document(getSemesterId())
            .collection(postPath)

    suspend fun getSectionList(courseId: String): List<Section> {
        return getSectionCollectionPath()
            .whereActiveData()
            .whereEqualTo("course.id", courseId)
            .get()
            .await()
            .map { it.toObject<Section>().copy(id = it.id) }
            .sortedBy { it.name }
    }

    suspend fun getSectionData(sectionId: String?): Section {
        if (sectionId == null) return Section()

        Timber.d("Section ID is not null")

        val response = getSectionCollectionPath()
            .document(sectionId)
            .get()
            .await()

        return response.toObject<Section>()?.copy(id = response.id)!!
    }

    suspend fun alreadySectionCreated(sectionName: String, courseId: String): Boolean {
        val section = getSectionCollectionPath()
            .whereEqualTo("course.id", courseId)
            .whereEqualTo("name", sectionName)
            .get()
            .await()

        return section.isEmpty.not()
    }

    suspend fun saveSection(section: Section) {
        val newSection = section.copy(
            updatedOn = Timestamp.now(),
            updaterEmail = getUserProfile().diuEmail,
            updaterId = getUserProfile().id
        )

        val sectionId = if (newSection.id.isEmpty()) {
            val id = getSectionCollectionPath().add(newSection).await().id

            val idData = mapOf(
                "id" to id
            )

            getSectionCollectionPath().document(id).set(idData, SetOptions.merge()).await()

            id
        } else {
            getSectionCollectionPath()
                .document(newSection.id)
                .set(newSection, SetOptions.merge())
                .await()

            newSection.id
        }

        // Keeping history
        getSectionCollectionPath()
            .document(sectionId)
            .collection(historyPath)
            .add(newSection)
            .await()
    }

    suspend fun joinSection(sectionId: String): Student {
        getSectionCollectionPath()
            .document(sectionId)
            .collection(studentPath)
            .document(getUserProfile().id)
            .set(getUserProfile().toMemberStudent())
            .await()

        val updatedJoinedSection = getUserProfile().joinedSection + sectionId
        val updatedProfile = getUserProfile().copy(joinedSection = updatedJoinedSection)

        profileRepository.saveUserProfile(updatedProfile)
        _userProfile = updatedProfile

        return updatedProfile
    }

    suspend fun leaveSection(sectionId: String): Student {
        getSectionCollectionPath()
            .document(sectionId)
            .collection(studentPath)
            .document(getUserProfile().id)
            .delete()
            .await()

        val updatedJoinedSection = getUserProfile().joinedSection.filter { it != sectionId }
        val updatedProfile = getUserProfile().copy(joinedSection = updatedJoinedSection)

        profileRepository.saveUserProfile(updatedProfile)
        _userProfile = updatedProfile

        return updatedProfile
    }

    suspend fun getSectionRoutineList(sectionId: String): List<Routine> {
        return getRoutineCollectionPath()
            .whereActiveData()
            .whereEqualTo("sectionId", sectionId)
            .get()
            .await()
            .map { it.toObject<Routine>().copy(id = it.id) }
            .sortedWith { left, right ->
                val leftIndex = Week.values().indexOfFirst {
                    left.day.equals(it.name, true)
                }
                val rightIndex = Week.values().indexOfFirst {
                    right.day.equals(it.name, true)
                }

                when {
                    leftIndex > rightIndex -> 1
                    leftIndex < rightIndex -> -1
                    else -> 0
                }
            }
    }

    suspend fun saveRoutine(routine: Routine) {
        val newRoutine = routine.copy(
            updaterId = getUserProfile().id,
            updaterEmail = getUserProfile().diuEmail,
            updatedOn = Timestamp.now()
        )

        val routineId = if (newRoutine.id.isEmpty()) {
            getRoutineCollectionPath()
                .add(newRoutine)
                .await()
                .id
        } else {
            getRoutineCollectionPath()
                .document(newRoutine.id)
                .set(newRoutine, SetOptions.merge())
                .await()

            newRoutine.id
        }

        // keeping history
        getRoutineCollectionPath()
            .document(routineId)
            .collection(historyPath)
            .add(newRoutine.copy(id = routineId))
    }

    suspend fun deleteRoutine(routineId: String) {
        getRoutineCollectionPath()
            .document(routineId)
            .delete()
            .await()
    }

    suspend fun getJoinedSectionList(): List<Section> {
        val joinedSections = getUserProfile().joinedSection

        if (joinedSections.isEmpty()) return emptyList()

        return getSectionCollectionPath()
            .whereActiveData()
            .whereIn("id", joinedSections)
            .get()
            .await()
            .map { it.toObject<Section>().copy(id = it.id) }
    }

    suspend fun getStudentRoutineList(): List<Routine> {
        val joinedSections = getUserProfile().joinedSection

        if (joinedSections.isEmpty()) return emptyList()

        return getRoutineCollectionPath()
            .whereActiveData()
            .whereIn("sectionId", joinedSections)
            .get()
            .await()
            .map { it.toObject<Routine>().copy(id = it.id) }
    }

    suspend fun getTodayPostList(date: String): List<Post> {
        val joinedSections = getUserProfile().joinedSection

        if (joinedSections.isEmpty()) return emptyList()

        return getPostCollectionPath()
            .whereActiveData()
            .whereIn("sectionId", joinedSections)
            .whereEqualTo("date", date)
            .get()
            .await()
            .map {
                val postTypeStr = it.getString("postType")!!

                when (PostType.valueOf(postTypeStr)) {
                    PostType.Routine -> Quiz() // this won't happen.
                    PostType.Quiz -> it.toObject<Quiz>().copy(id = it.id)
                    PostType.Assignment -> it.toObject<Assignment>().copy(id = it.id)
                    PostType.Presentation -> it.toObject<Presentation>().copy(id = it.id)
                    PostType.Project -> it.toObject<Project>().copy(id = it.id)
                }
            }
    }

    suspend fun getTodayTodoList(currentDateMillis: Long): List<Post> {
        val joinedSections = getUserProfile().joinedSection

        if (joinedSections.isEmpty()) return emptyList()

        return getPostCollectionPath()
            .whereActiveData()
            .whereIn("sectionId", joinedSections)
            .whereGreaterThanOrEqualTo("dateTimeMillis", currentDateMillis)
            .get()
            .await()
            .map {
                val postTypeStr = it.getString("postType")!!

                when (PostType.valueOf(postTypeStr)) {
                    PostType.Routine -> Quiz() // this won't happen.
                    PostType.Quiz -> it.toObject<Quiz>().copy(id = it.id)
                    PostType.Assignment -> it.toObject<Assignment>().copy(id = it.id)
                    PostType.Presentation -> it.toObject<Presentation>().copy(id = it.id)
                    PostType.Project -> it.toObject<Project>().copy(id = it.id)
                }
            }
    }

    suspend fun getPost(postType: PostType, postId: String?): Post {
        if (postId == null) {
            val currentDate = Calendar.getInstance(Locale.getDefault()).toDateString()
            return when (postType) {
                PostType.Quiz -> Quiz().copy(date = currentDate)
                PostType.Assignment -> Assignment().copy(date = currentDate)
                PostType.Presentation -> Presentation().copy(date = currentDate)
                PostType.Project -> Project().copy(date = currentDate)
                PostType.Routine -> Quiz().copy(date = currentDate)
            }
        }

        val response = getPostCollectionPath()
            .document(postId)
            .get()
            .await()

        return when (postType) {
            PostType.Quiz -> response.toObject<Quiz>()!!.copy(id = response.id)
            PostType.Assignment -> response.toObject<Assignment>()!!.copy(id = response.id)
            PostType.Presentation -> response.toObject<Presentation>()!!.copy(id = response.id)
            PostType.Project -> response.toObject<Project>()!!.copy(id = response.id)
            PostType.Routine -> Quiz() // this won't happen
        }
    }

    suspend fun savePost(post: Post) {
        val newPost = when (post) {
            is Quiz -> post.copy(
                updaterId = getUserProfile().id,
                updaterEmail = getUserProfile().diuEmail,
                updatedOn = Timestamp.now()
            )
            is Assignment -> post.copy(
                updaterId = getUserProfile().id,
                updaterEmail = getUserProfile().diuEmail,
                updatedOn = Timestamp.now()
            )
            is Presentation -> post.copy(
                updaterId = getUserProfile().id,
                updaterEmail = getUserProfile().diuEmail,
                updatedOn = Timestamp.now()
            )
            is Project -> post.copy(
                updaterId = getUserProfile().id,
                updaterEmail = getUserProfile().diuEmail,
                updatedOn = Timestamp.now()
            )
        }

        val postId = if (newPost.id.isEmpty()) {
            val id = getPostCollectionPath().add(newPost).await().id
            val idData = mapOf("id" to id)
            getPostCollectionPath().document(id).set(idData, SetOptions.merge()).await()

            id
        } else {
            getPostCollectionPath()
                .document(newPost.id)
                .set(newPost, SetOptions.merge())
                .await()

            newPost.id
        }

        // Keeping history
        getPostCollectionPath()
            .document(postId)
            .collection(historyPath)
            .add(newPost)
    }

    suspend fun getJoinedGroup(postId: String, sectionId: String): Group? {
        val groups = getGroupList(postId)

        val memberStudent: MemberStudent = getSectionCollectionPath()
            .document(sectionId)
            .collection(studentPath)
            .document(getUserProfile().id)
            .get()
            .await()
            .toObject<MemberStudent>() ?: return null

        return groups.find { memberStudent.joinedGroups.contains(it.id) }
    }

    suspend fun deletePost(post: Post) {
        val newPost = when (post) {
            is Quiz -> post.copy(
                isActive = false,
                updaterId = getUserProfile().id,
                updaterEmail = getUserProfile().diuEmail,
                updatedOn = Timestamp.now()
            )
            is Assignment -> post.copy(
                isActive = false,
                updaterId = getUserProfile().id,
                updaterEmail = getUserProfile().diuEmail,
                updatedOn = Timestamp.now()
            )
            is Presentation -> post.copy(
                isActive = false,
                updaterId = getUserProfile().id,
                updaterEmail = getUserProfile().diuEmail,
                updatedOn = Timestamp.now()
            )
            is Project -> post.copy(
                isActive = false,
                updaterId = getUserProfile().id,
                updaterEmail = getUserProfile().diuEmail,
                updatedOn = Timestamp.now()
            )
        }

        getPostCollectionPath()
            .document(newPost.id)
            .set(newPost, SetOptions.merge())
            .await()

        // Keeping history
        getPostCollectionPath()
            .document(newPost.id)
            .collection(historyPath)
            .add(newPost)
    }

    suspend fun getMemberStudentList(sectionId: String): List<MemberStudent> {
        return getSectionCollectionPath()
            .document(sectionId)
            .collection(studentPath)
            .get()
            .await()
            .map { it.toObject<MemberStudent>() }
    }

    suspend fun getGroupList(postId: String): List<Group> {
        return getPostCollectionPath()
            .document(postId)
            .collection(groupPath)
            .whereActiveData()
            .get()
            .await()
            .map { it.toObject<Group>().copy(id = it.id) }
    }

    suspend fun editGroup(postId: String, group: Group) {
        val newGroup = group.copy(
            updatedOn = Timestamp.now(),
            updaterEmail = getUserProfile().diuEmail,
            updaterId = getUserProfile().id
        )

        val groupPath = getPostCollectionPath()
            .document(postId)
            .collection(groupPath)

        val groupId = if (newGroup.id.isEmpty()) {
            groupPath.add(newGroup).await().id
        } else {
            groupPath.document(newGroup.id)
                .set(newGroup, SetOptions.merge())
                .await()

            newGroup.id
        }

        // Keeping history
        groupPath.document(groupId)
            .collection(historyPath)
            .add(newGroup.copy(id = groupId))
    }

    suspend fun joinLeavePostGroup(profileMember: MemberStudent, sectionId: String) {
        getSectionCollectionPath()
            .document(sectionId)
            .collection(studentPath)
            .document(profileMember.studentId)
            .set(profileMember, SetOptions.merge())
            .await()
    }
}