package com.example.vadabarder.data.repository

import android.app.Application
import androidx.lifecycle.LiveData
import com.example.vadabarder.data.database.VadaBarberDatabase
import com.example.vadabarder.data.model.Cita
import java.util.concurrent.Executor
import java.util.concurrent.Executors

class CitaRepository(application: Application) {

    private val citaDao = VadaBarberDatabase.getInstance(application).citaDao()
    private val executor: Executor = Executors.newSingleThreadExecutor()

    // Filtra las citas por el usuario activo
    fun obtenerCitas(userId: String): LiveData<List<Cita>> = citaDao.obtenerPorUsuario(userId)

    fun insertar(cita: Cita) {
        executor.execute { citaDao.insertar(cita) }
    }

    fun eliminar(cita: Cita) {
        executor.execute { citaDao.eliminar(cita) }
    }
}
