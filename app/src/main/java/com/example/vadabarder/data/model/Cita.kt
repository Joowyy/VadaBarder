package com.example.vadabarder.data.model

// Constructor vacío requerido por Firestore para deserializar DocumentSnapshot → Cita
data class Cita(
    val id: String = "",
    val userId: String = "",
    val fecha: String = "",
    val hora: String = "",
    val servicio: String = "",
    val precio: String = ""
)
