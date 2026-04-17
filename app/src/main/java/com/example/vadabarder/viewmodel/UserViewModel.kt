package com.example.vadabarder.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.vadabarder.data.model.Cita

class UserViewModel : ViewModel(){

    var user: String? = null
    var correo: String? = null
    var psswd: String? = null
    val historialCitas = MutableLiveData<List<Cita>>(emptyList())

    fun limpiarViewModel() {
        user = ""
        correo = ""
        psswd = ""
        historialCitas.value = emptyList()
    }

    fun agregarCita(cita: Cita) {
        val listaActual = historialCitas.value?.toMutableList() ?: mutableListOf()
        listaActual.add(0, cita) // agregamos al inicio para mostrar la más reciente primero
        historialCitas.value = listaActual
    }


}