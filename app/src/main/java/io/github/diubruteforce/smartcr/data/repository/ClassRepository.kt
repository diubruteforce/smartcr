package io.github.diubruteforce.smartcr.data.repository

import com.google.firebase.Timestamp
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import io.github.diubruteforce.smartcr.model.data.Course
import io.github.diubruteforce.smartcr.model.data.Department
import io.github.diubruteforce.smartcr.model.data.Section
import io.github.diubruteforce.smartcr.model.data.Student
import io.github.diubruteforce.smartcr.utils.extension.whereActiveData
import kotlinx.coroutines.tasks.await
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
    private val historyPath = "history"

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

    private suspend fun getUserProfile(): Student {
        if (_userProfile != null) return _userProfile!!

        _userProfile = profileRepository.getUserProfile()

        return _userProfile!!
    }

    private suspend fun getSemesterId(): String {
        if (_semesterId != null) return _semesterId!!

        _semesterId = db.collection(departmentPath)
            .document(getUserProfile().id)
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
    }

    private suspend fun getSectionCollectionPath() =
        db.collection(departmentPath)
            .document(getUserProfile().departmentId)
            .collection(semesterPath)
            .document(getSemesterId())
            .collection(sectionPath)

    suspend fun getSectionList(courseId: String): List<Section> {
        return getSectionCollectionPath()
            .whereActiveData()
            .whereEqualTo("courseId", courseId)
            .get()
            .await()
            .map { it.toObject<Section>().copy(id = it.id) }
    }

    suspend fun getSectionData(sectionId: String?): Section {
        if (sectionId == null) return Section()

        val response = getSectionCollectionPath()
            .document(sectionId)
            .get()
            .await()

        return response.toObject<Section>()?.copy(id = response.id)!!
    }

    suspend fun saveSection(section: Section) {
        val newSection = section.copy(
            updatedOn = Timestamp.now(),
            updaterEmail = getUserProfile().diuEmail,
            updaterId = getUserProfile().id
        )

        val sectionId = if (newSection.id.isEmpty()) {
            getSectionCollectionPath().add(newSection).await().id
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
}