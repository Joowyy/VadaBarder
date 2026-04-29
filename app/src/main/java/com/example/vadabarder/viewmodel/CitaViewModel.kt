package com.example.vadabarder.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.switchMap
import com.example.vadabarder.data.model.Cita
import com.example.vadabarder.data.repository.CitaRepository

class CitaViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = CitaRepository(application)

    // userId activo; cuando cambia, 'citas' se re-consulta automáticamente
    private val _userId = MutableLiveData<String>()

    // switchMap: cada vez que _userId cambia, lanza una nueva consulta a Room
    val citas: LiveData<List<Cita>> = _userId.switchMap { uid ->
        repository.obtenerCitas(uid)
    }

    /** Llamar justo después del login o al entrar en ProfileFragment */
    fun setUsuario(userId: String) {
        _userId.value = userId
    }

    fun limpiarUsuario() { _userId.value = "" }

    fun insertar(cita: Cita) = repository.insertar(cita)

    fun eliminar(cita: Cita) = repository.eliminar(cita)
}
