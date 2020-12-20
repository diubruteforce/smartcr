package io.github.diubruteforce.smartcr.data.repository

import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

object ClassRepository {
    private val db by lazy { Firebase.firestore }

}