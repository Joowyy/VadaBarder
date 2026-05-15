package com.example.vadabarder.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.vadabarder.data.model.Cita
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.ListenerRegistration

// Repositorio de citas — única puerta a Firestore.
// Las reservas viven en una colección global "reservas/" para que cualquier
// usuario autenticado pueda saber qué huecos están ocupados.
class CitaFirestoreRepository {

    private val db = FirebaseFirestore.getInstance()
    private val reservasRef = db.collection("reservas")

    // Listeners separados — historial del usuario y huecos del día.
    private var listenerHistorial: ListenerRegistration? = null
    private var listenerHoras: ListenerRegistration? = null

    // ID determinista por hueco: "15-05-2026_10:00".
    // Sirve para que dos usuarios no puedan crear el mismo documento.
    private fun construirId(fecha: String, hora: String): String =
        "${fecha.replace("/", "-")}_$hora"

    // Historial del usuario — filtra la colección global por userId.
    fun observarCitas(uid: String): LiveData<List<Cita>> {
        val liveData = MutableLiveData<List<Cita>>()
        listenerHistorial?.remove()
        listenerHistorial = reservasRef
            .whereEqualTo("userId", uid)
            .orderBy("fecha")
            .addSnapshotListener { snapshot, error ->
                if (error != null) return@addSnapshotListener
                val citas = snapshot?.documents
                    ?.mapNotNull { it.toObject(Cita::class.java) }
                    ?: emptyList()
                liveData.postValue(citas)
            }
        return liveData
    }

    // Huecos ocupados de un día — todos los usuarios ven la misma lista.
    fun observarHorasOcupadas(fecha: String): LiveData<Set<String>> {
        val liveData = MutableLiveData<Set<String>>()
        listenerHoras?.remove()
        listenerHoras = reservasRef
            .whereEqualTo("fecha", fecha)
            .addSnapshotListener { snapshot, error ->
                if (error != null) return@addSnapshotListener
                val horas = snapshot?.documents
                    ?.mapNotNull { it.getString("hora") }
                    ?.toSet()
                    ?: emptySet()
                liveData.postValue(horas)
            }
        return liveData
    }

    // Inserta con transacción — aborta si el hueco ya existe.
    fun insertar(cita: Cita, callback: (Boolean, String?) -> Unit) {
        val id = construirId(cita.fecha, cita.hora)
        val ref = reservasRef.document(id)
        val citaConId = cita.copy(id = id)

        db.runTransaction { tx ->
            val snap = tx.get(ref)
            if (snap.exists()) {
                throw FirebaseFirestoreException(
                    "Esa hora ya está reservada",
                    FirebaseFirestoreException.Code.ABORTED
                )
            }
            tx.set(ref, citaConId)
            null
        }
            .addOnSuccessListener { callback(true, null) }
            .addOnFailureListener { callback(false, it.message) }
    }

    fun eliminar(citaId: String, callback: (Boolean, String?) -> Unit) {
        reservasRef.document(citaId)
            .delete()
            .addOnSuccessListener { callback(true, null) }
            .addOnFailureListener { callback(false, it.message) }
    }

    fun detenerEscucha() {
        listenerHistorial?.remove()
        listenerHistorial = null
        listenerHoras?.remove()
        listenerHoras = null
    }
}
