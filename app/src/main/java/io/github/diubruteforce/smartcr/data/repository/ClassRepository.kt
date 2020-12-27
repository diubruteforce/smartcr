package io.github.diubruteforce.smartcr.data.repository

import com.google.firebase.Timestamp
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import io.github.diubruteforce.smartcr.model.data.*
import io.github.diubruteforce.smartcr.utils.extension.whereActiveData
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ClassRepository @Inject constructor(
    val profileRepository: ProfileRepository
) {
    private val db by lazy { Firebase.firestore }
    private val departmentPath = "department"
    private val coursePath = "course"
    private val semesterPath = "semester"
    private val sectionPath = "section"
    private val studentPath = "student"
    private val historyPath = "history"
    private val routinePath = "routine"

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

    private suspend fun getSemesterId(): String {
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
        return getSectionCollectionPath()
            .whereActiveData()
            .whereIn("id", getUserProfile().joinedSection)
            .get()
            .await()
            .map { it.toObject<Section>().copy(id = it.id) }
    }
}