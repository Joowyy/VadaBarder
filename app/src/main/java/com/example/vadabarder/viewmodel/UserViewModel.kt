package com.example.vadabarder.viewmodel

import androidx.lifecycle.ViewModel

class UserViewModel : ViewModel() {

    var user: String? = null
    var correo: String? = null
    var psswd: String? = null

    fun limpiarViewModel() {
        user = ""
        correo = ""
        psswd = ""
    }
}
