package io.github.diubruteforce.smartcr.data.repository

import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import io.github.diubruteforce.smartcr.model.data.Department
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class ClassRepository @Inject constructor() {
    private val db by lazy { Firebase.firestore }
    private val departmentPath = "department"

    suspend fun getAllDepartment(): List<Department> {

        val result = db.collection(departmentPath).get().await()

        return result.documents.mapNotNull {
            it.toObject(Department::class.java)?.copy(id = it.id)
        }
    }

}