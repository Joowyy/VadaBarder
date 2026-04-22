package com.example.vadabarder.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.example.vadabarder.data.model.Cita
import com.example.vadabarder.data.repository.CitaRepository

class CitaViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = CitaRepository(application)

    val citas: LiveData<List<Cita>> = repository.obtenerCitas()

    fun insertar(cita: Cita) = repository.insertar(cita)

    fun eliminar(cita: Cita) = repository.eliminar(cita)
}
