package com.example.vadabarder.data.prefs

import android.content.Context

// Guarda la preferencia "Recordar usuario" para decidir si la sesión de Firebase
// debe sobrevivir entre lanzamientos de la app. Firebase Auth siempre persiste
// el token localmente, así que al arrancar leemos esta flag y, si es false,
// hacemos signOut() para forzar la pantalla de login.
object SessionPreferences {

    private const val PREFS_NAME = "vadabarber_session"
    private const val KEY_REMEMBER = "remember_user"

    fun setRecordar(context: Context, recordar: Boolean) {
        prefs(context).edit().putBoolean(KEY_REMEMBER, recordar).apply()
    }

    fun isRecordar(context: Context): Boolean =
        prefs(context).getBoolean(KEY_REMEMBER, false)

    private fun prefs(context: Context) =
        context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
}
