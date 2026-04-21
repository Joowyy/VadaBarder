package com.example.vadabarder.data

import java.util.Calendar

object BarberiaData {

    // ── Servicios ─────────────────────────────────────────────────────────
    val servicios = linkedMapOf(
        "Corte clásico"    to 12,
        "Fade / Degradado" to 14,
        "Arreglo de barba" to 8,
        "Perfilado"        to 6,
        "Corte + Barba"    to 20,
        "Afeitado navaja"  to 15,
        "Diseño de barba"  to 10,
        "Cejas"            to 5
    )

    val serviciosPopulares = listOf(
        "Corte clásico", "Fade / Degradado", "Arreglo de barba",
        "Perfilado", "Corte + Barba"
    )

    // ── Horario ───────────────────────────────────────────────────────────
    val horasMañana = listOf(
        "09:00", "09:30", "10:00", "10:30", "11:00", "11:30",
        "12:00", "12:30", "13:00", "13:30"
    )
    val horasTarde = listOf(
        "16:00", "16:30", "17:00", "17:30", "18:00", "18:30",
        "19:00", "19:30"
    )

    val horarioSemana = linkedMapOf(
        "Lunes — Viernes" to "09:00–14:00  ·  16:00–20:00",
        "Sábado"          to "09:00–14:00",
        "Domingo"         to null
    )

    // Horas disponibles según día de la semana (Calendar.DAY_OF_WEEK)
    fun horasPorDia(diaSemana: Int): List<String>? = when (diaSemana) {
        Calendar.SUNDAY                                          -> null
        Calendar.SATURDAY                                        -> horasMañana
        else                                                     -> horasMañana + horasTarde
    }
}
