package com.example.vadabarder.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.vadabarder.data.model.AuthState
import com.example.vadabarder.data.repository.AuthRepository
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest

class AuthViewModel : ViewModel() {

    private val repository = AuthRepository()

    private val _authState = MutableLiveData<AuthState<FirebaseUser>?>()
    val authState: LiveData<AuthState<FirebaseUser>?> get() = _authState

    fun login(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _authState.value = AuthState.Error("Completa todos los campos")
            return
        }
        _authState.value = AuthState.Loading
        repository.login(email, password) { user, error ->
            _authState.value = if (user != null) AuthState.Success(user)
                               else AuthState.Error(error ?: "Error desconocido")
        }
    }

    fun registrar(nombre: String, email: String, password: String) {
        if (nombre.isBlank() || email.isBlank() || password.isBlank()) {
            _authState.value = AuthState.Error("Completa todos los campos")
            return
        }
        _authState.value = AuthState.Loading
        repository.registrar(email, password) { user, error ->
            if (user != null) {
                val updates = UserProfileChangeRequest.Builder()
                    .setDisplayName(nombre)
                    .build()
                user.updateProfile(updates).addOnCompleteListener {
                    _authState.value = AuthState.Success(user)
                }
            } else {
                _authState.value = AuthState.Error(error ?: "Error desconocido")
            }
        }
    }

    fun getCurrentUser(): FirebaseUser? = repository.getCurrentUser()

    fun cerrarSesion() {
        repository.logout()
        _authState.value = null
    }
}
