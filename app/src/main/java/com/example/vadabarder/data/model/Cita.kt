package com.example.vadabarder.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "citas")
data class Cita(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    // FK lógica al usuario propietario; cuando llegue Firebase Auth aquí irá su UID
    val userId: String,
    val fecha: String,
    val hora: String,
    val servicio: String,
    val precio: String

)