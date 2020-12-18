package io.github.diubruteforce.smartcr.data.repository

import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await


object AuthRepository {
    private val firebaseAuth = Firebase.auth
    private val user get() = firebaseAuth.currentUser

    val isAuthenticated: Boolean get() = user != null
    val isEmailVerified: Boolean get() = user?.isEmailVerified ?: false

    suspend fun sendVerificationEmail(): Void =
        user!!.sendEmailVerification().await()


    suspend fun requestPasswordReset(email: String): Void =
        firebaseAuth.sendPasswordResetEmail(email).await()


    suspend fun createNewUser(email: String, password: String): AuthResult =
        firebaseAuth.createUserWithEmailAndPassword(email, password).await()

    suspend fun signIn(email: String, password: String): AuthResult =
        firebaseAuth.signInWithEmailAndPassword(email, password).await()

    fun signOut(): Unit = firebaseAuth.signOut()
}