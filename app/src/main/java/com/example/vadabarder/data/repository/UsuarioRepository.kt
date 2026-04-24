package com.example.vadabarder.data.repository

import android.app.Application
import android.os.Handler
import android.os.Looper
import com.example.vadabarder.data.database.VadaBarberDatabase
import com.example.vadabarder.data.model.Usuario
import java.util.concurrent.Executor
import java.util.concurrent.Executors

class UsuarioRepository(application: Application) {

    private val usuarioDao = VadaBarberDatabase.getInstance(application).usuarioDao()
    private val executor: Executor = Executors.newSingleThreadExecutor()

    // Inserción en hilo secundario (mismo patrón que CitaRepository)
    fun insertar(usuario: Usuario) {
        executor.execute { usuarioDao.insertar(usuario) }
    }

    // La consulta corre en segundo plano; el resultado vuelve al hilo principal via callback
    fun buscarPorNombre(nombre: String, callback: (Usuario?) -> Unit) {
        executor.execute {
            val usuario = usuarioDao.buscarPorNombre(nombre)
            Handler(Looper.getMainLooper()).post { callback(usuario) }
        }
    }
}
