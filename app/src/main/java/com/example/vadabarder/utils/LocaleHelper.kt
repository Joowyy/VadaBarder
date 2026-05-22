package com.example.vadabarder.utils

import android.content.Context
import android.content.res.Configuration
import java.util.Locale

/**
 * Gestiona el idioma de la aplicación de forma persistente.
 *
 * Uso:
 *  - Llamar a [wrap] en [attachBaseContext] de MainActivity para que cada
 *    Activity se cree con el locale guardado.
 *  - Llamar a [setLocale] + reiniciar la Activity para cambiar el idioma en
 *    tiempo de ejecución.
 */
object LocaleHelper {

    private const val PREFS_NAME  = "vadabarber_settings"
    private const val KEY_LOCALE  = "app_locale"
    const val DEFAULT_LOCALE = "es"

    /** Devuelve el código de idioma guardado ("es", "en" o "fr"). */
    fun getLocale(context: Context): String =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getString(KEY_LOCALE, DEFAULT_LOCALE) ?: DEFAULT_LOCALE

    /** Persiste el código de idioma seleccionado por el usuario. */
    fun setLocale(context: Context, locale: String) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putString(KEY_LOCALE, locale)
            .apply()
    }

    /**
     * Envuelve el contexto base con el locale guardado.
     * Llamar desde [android.app.Activity.attachBaseContext].
     */
    fun wrap(context: Context): Context {
        val lang   = getLocale(context)
        val locale = Locale(lang)
        Locale.setDefault(locale)
        val config = Configuration(context.resources.configuration)
        config.setLocale(locale)
        return context.createConfigurationContext(config)
    }
}
