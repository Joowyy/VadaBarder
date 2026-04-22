package com.example.vadabarder.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "citas")
data class Cita(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val fecha: String,
    val hora: String,
    val servicio: String,
    val precio: String

)