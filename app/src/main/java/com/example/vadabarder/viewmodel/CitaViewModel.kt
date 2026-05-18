package com.example.vadabarder.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.switchMap
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.vadabarder.data.model.AuthState
import com.example.vadabarder.data.model.Cita
import com.example.vadabarder.data.repository.CitaFirestoreRepository
import com.example.vadabarder.notifications.CitaReminderWorker
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.TimeUnit

class CitaViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = CitaFirestoreRepository()
    private val workManager = WorkManager.getInstance(application)

    private val _userId = MutableLiveData<String>()
    private val _fechaConsulta = MutableLiveData<String>()

    val citas: LiveData<List<Cita>> = _userId.switchMap { uid ->
        if (uid.isBlank()) MutableLiveData(emptyList())
        else repository.observarCitas(uid)
    }

    val horasOcupadas: LiveData<Set<String>> = _fechaConsulta.switchMap { fecha ->
        if (fecha.isBlank()) MutableLiveData(emptySet())
        else repository.observarHorasOcupadas(fecha)
    }

    private val _insertState = MutableLiveData<AuthState<Unit>?>()
    val insertState: LiveData<AuthState<Unit>?> get() = _insertState

    fun setUsuario(userId: String) {
        if (_userId.value != userId) _userId.value = userId
    }

    fun consultarHorasDe(fecha: String) { _fechaConsulta.value = fecha }

    fun limpiarUsuario() {
        _userId.value = ""
        _fechaConsulta.value = ""
        repository.detenerEscucha()
    }

    fun insertar(cita: Cita) {
        _insertState.value = AuthState.Loading
        repository.insertar(cita) { ok, error ->
            if (ok) {
                programarRecordatorios(cita)
                _insertState.value = AuthState.Success(Unit)
            } else {
                _insertState.value = AuthState.Error(error ?: "Error al guardar la cita")
            }
        }
    }

    fun limpiarInsertState() { _insertState.value = null }

    fun eliminar(citaId: String) {
        cancelarRecordatorios(citaId)
        repository.eliminar(citaId) { _, _ -> }
    }

    private fun programarRecordatorios(cita: Cita) {
        val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        val citaMs = try {
            sdf.parse("${cita.fecha} ${cita.hora}")?.time ?: return
        } catch (e: Exception) { return }

        val ahora = System.currentTimeMillis()
        // ID determinista igual que CitaFirestoreRepository.construirId
        val citaId = "${cita.fecha.replace("/", "-")}_${cita.hora}"

        val datosBase = Data.Builder()
            .putString("servicio", cita.servicio)
            .putString("fecha", cita.fecha)
            .putString("hora", cita.hora)
            .build()

        val delay24h = citaMs - TimeUnit.HOURS.toMillis(24) - ahora
        if (delay24h > 0) {
            workManager.enqueue(
                OneTimeWorkRequestBuilder<CitaReminderWorker>()
                    .setInitialDelay(delay24h, TimeUnit.MILLISECONDS)
                    .setInputData(
                        Data.Builder().putAll(datosBase).putString("tipo", "24h").build()
                    )
                    .addTag("vada_${citaId}_24h")
                    .build()
            )
        }

        val delay1h = citaMs - TimeUnit.HOURS.toMillis(1) - ahora
        if (delay1h > 0) {
            workManager.enqueue(
                OneTimeWorkRequestBuilder<CitaReminderWorker>()
                    .setInitialDelay(delay1h, TimeUnit.MILLISECONDS)
                    .setInputData(
                        Data.Builder().putAll(datosBase).putString("tipo", "1h").build()
                    )
                    .addTag("vada_${citaId}_1h")
                    .build()
            )
        }
    }

    private fun cancelarRecordatorios(citaId: String) {
        workManager.cancelAllWorkByTag("vada_${citaId}_24h")
        workManager.cancelAllWorkByTag("vada_${citaId}_1h")
    }

    override fun onCleared() {
        super.onCleared()
        repository.detenerEscucha()
    }
}
