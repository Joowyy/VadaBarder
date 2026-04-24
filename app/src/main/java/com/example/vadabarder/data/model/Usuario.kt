package com.example.vadabarder.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

// Cuando llegue Firebase Auth, el uid será el que devuelve FirebaseAuth.currentUser?.uid
// Por ahora generamos un UUID aleatorio en el ViewModel al registrar
@Entity(tableName = "usuarios")
data class Usuario(
    @PrimaryKey
    val uid: String,
    val nombre: String,
    val correo: String
)
