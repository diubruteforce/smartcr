package io.github.diubruteforce.smartcr.data.repository

import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import io.github.diubruteforce.smartcr.model.data.Course
import io.github.diubruteforce.smartcr.model.data.Department
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class ClassRepository @Inject constructor(
    val profileRepository: ProfileRepository
) {
    private val db by lazy { Firebase.firestore }
    private val departmentPath = "department"
    private val coursePath = "course"

    suspend fun getAllDepartment(): List<Department> {

        val result = db.collection(departmentPath).get().await()

        return result.documents.mapNotNull {
            it.toObject(Department::class.java)?.copy(id = it.id)
        }.sortedBy { it.name }
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