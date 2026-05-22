package com.example.vadabarder.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class AuthRepository {

    private val auth = FirebaseAuth.getInstance()

    fun login(email: String, password: String, callback: (FirebaseUser?, String?) -> Unit) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) callback(auth.currentUser, null)
                else callback(null, task.exception?.message ?: "Error al iniciar sesión")
            }
    }

    fun registrar(email: String, password: String, callback: (FirebaseUser?, String?) -> Unit) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) callback(auth.currentUser, null)
                else callback(null, task.exception?.message ?: "Error al crear la cuenta")
            }
    }

    fun getCurrentUser(): FirebaseUser? = auth.currentUser

    fun logout() = auth.signOut()
}
