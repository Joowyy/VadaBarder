package com.example.vadabarder.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import com.example.vadabarder.data.model.AuthState
import com.example.vadabarder.data.model.Cita
import com.example.vadabarder.data.repository.CitaFirestoreRepository

class CitaViewModel : ViewModel() {

    private val repository = CitaFirestoreRepository()

    private val _userId = MutableLiveData<String>()
    private val _fechaConsulta = MutableLiveData<String>()

    // switchMap: cada vez que _userId cambia, lanza un nuevo SnapshotListener en Firestore.
    val citas: LiveData<List<Cita>> = _userId.switchMap { uid ->
        if (uid.isBlank()) MutableLiveData(emptyList())
        else repository.observarCitas(uid)
    }

    // Horas ya reservadas para la fecha consultada — se recalcula en tiempo real.
    val horasOcupadas: LiveData<Set<String>> = _fechaConsulta.switchMap { fecha ->
        if (fecha.isBlank()) MutableLiveData(emptySet())
        else repository.observarHorasOcupadas(fecha)
    }

    private val _insertState = MutableLiveData<AuthState<Unit>?>()
    val insertState: LiveData<AuthState<Unit>?> get() = _insertState

    fun setUsuario(userId: String) { _userId.value = userId }

    fun consultarHorasDe(fecha: String) { _fechaConsulta.value = fecha }

    fun limpiarUsuario() {
        _userId.value = ""
        _fechaConsulta.value = ""
        repository.detenerEscucha()
    }

    fun insertar(cita: Cita) {
        _insertState.value = AuthState.Loading
        repository.insertar(cita) { ok, error ->
            _insertState.value = if (ok) AuthState.Success(Unit)
                                 else AuthState.Error(error ?: "Error al guardar la cita")
        }
    }

    fun limpiarInsertState() { _insertState.value = null }

    fun eliminar(citaId: String) {
        // El SnapshotListener actualiza la UI automáticamente al borrar
        repository.eliminar(citaId) { _, _ -> }
    }

    override fun onCleared() {
        super.onCleared()
        repository.detenerEscucha()
    }
}
