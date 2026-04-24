package com.example.vadabarder.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.vadabarder.data.model.Usuario
import com.example.vadabarder.data.repository.UsuarioRepository
import java.util.UUID

class UserViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = UsuarioRepository(application)

    // Usuario de la sesión activa (en memoria; se pierde si el proceso muere)
    // Cuando llegue Firebase Auth, aquí guardaremos FirebaseUser o solo el uid
    var usuarioActual: Usuario? = null

    /**
     * Crea un nuevo usuario en Room y lo pone como sesión activa.
     * El uid es un UUID aleatorio; Firebase Auth lo reemplazará por su propio uid.
     */
    fun registrar(nombre: String, correo: String) {
        val usuario = Usuario(
            uid = UUID.randomUUID().toString(),
            nombre = nombre,
            correo = correo
        )
        repository.insertar(usuario)   // hilo secundario (Executor en el Repository)
        usuarioActual = usuario         // sesión activa inmediata
    }

    /**
     * Busca el usuario por nombre en Room.
     * - onExito  → usuario encontrado, sesión iniciada
     * - onFallo  → nombre no registrado
     * El callback llega al hilo principal (Handler en el Repository).
     */
    fun login(nombre: String, onExito: () -> Unit, onFallo: () -> Unit) {
        repository.buscarPorNombre(nombre) { usuario ->
            if (usuario != null) {
                usuarioActual = usuario
                onExito()
            } else {
                onFallo()
            }
        }
    }

    /** Limpia la sesión activa al cerrar sesión */
    fun cerrarSesion() {
        usuarioActual = null
    }
}
