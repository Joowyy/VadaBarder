package com.example.vadabarder.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.vadabarder.data.model.Cita
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration

class CitaFirestoreRepository {

    private val db = FirebaseFirestore.getInstance()
    private var listener: ListenerRegistration? = null

    fun observarCitas(uid: String): LiveData<List<Cita>> {
        val liveData = MutableLiveData<List<Cita>>()
        listener?.remove()
        listener = db.collection("usuarios").document(uid)
            .collection("citas")
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

    fun insertar(cita: Cita, callback: (Boolean, String?) -> Unit) {
        val ref = db.collection("usuarios").document(cita.userId)
            .collection("citas").document()
        val citaConId = cita.copy(id = ref.id)
        ref.set(citaConId)
            .addOnSuccessListener { callback(true, null) }
            .addOnFailureListener { callback(false, it.message) }
    }

    fun eliminar(uid: String, citaId: String, callback: (Boolean, String?) -> Unit) {
        db.collection("usuarios").document(uid)
            .collection("citas").document(citaId)
            .delete()
            .addOnSuccessListener { callback(true, null) }
            .addOnFailureListener { callback(false, it.message) }
    }

    fun detenerEscucha() {
        listener?.remove()
        listener = null
    }
}
