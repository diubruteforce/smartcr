package io.github.diubruteforce.smartcr.data.repository

import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import io.github.diubruteforce.smartcr.model.data.Course
import io.github.diubruteforce.smartcr.model.data.Department
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
    private var _semesterId: String? = null
    private var _departmentId: String? = null

    suspend fun getAllDepartment(): List<Department> {
        val result = db.collection(departmentPath).get().await()

        return result.documents
            .mapNotNull {
                it.toObject(Department::class.java)?.copy(id = it.id)
            }
            .sortedBy { it.name }
    }

    private suspend fun getDepartmentId(): String {
        if (_departmentId != null) return _departmentId!!

        _departmentId = profileRepository.getUserProfile().departmentId

        return _departmentId!!
    }

    private suspend fun getSemesterId(): String {
        if (_semesterId != null) return _semesterId!!

        val departmentId = getDepartmentId()

        val a = db.collection(departmentPath).document(departmentId).collection(semesterPath)

        return ""
    }

    suspend fun getCourseList(): List<Course> {
        val userProfile = profileRepository.getUserProfile()

        return db.collection(departmentPath)
            .document(userProfile.departmentId)
            .collection(coursePath)
            .get()
            .await()
            .map { it.toObject<Course>().copy(id = it.id) }

    }
}