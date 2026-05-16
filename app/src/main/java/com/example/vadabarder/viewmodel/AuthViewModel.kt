package com.example.vadabarder.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.vadabarder.data.model.AuthState
import com.example.vadabarder.data.repository.AuthRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest

class AuthViewModel : ViewModel() {

    private val repository = AuthRepository()
    private val auth = FirebaseAuth.getInstance()

    // ── Estado de operaciones (login / registro) ─────────────────────────
    private val _authState = MutableLiveData<AuthState<FirebaseUser>?>()
    val authState: LiveData<AuthState<FirebaseUser>?> get() = _authState

    // ── Usuario activo — reactivo al AuthStateListener de Firebase ───────
    // Se inicializa con el valor sincrónico del disco para no mostrar null
    // en el primer frame, y se actualiza si Firebase cambia la sesión
    // (renovación de token, cierre de sesión, restauración tras process death).
    private val _currentUser = MutableLiveData<FirebaseUser?>(auth.currentUser)
    val currentUser: LiveData<FirebaseUser?> get() = _currentUser

    private val authStateListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
        _currentUser.postValue(firebaseAuth.currentUser)
    }

    init {
        auth.addAuthStateListener(authStateListener)
    }

    // ── Operaciones de auth ───────────────────────────────────────────────
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

    /** Lectura síncrona — úsala solo donde no haya un observer activo (ej. lógica puntual). */
    fun getCurrentUser(): FirebaseUser? = auth.currentUser

    fun cerrarSesion() {
        repository.logout()   // dispara el AuthStateListener → _currentUser = null
        _authState.value = null
    }

    override fun onCleared() {
        super.onCleared()
        auth.removeAuthStateListener(authStateListener)
    }
}
