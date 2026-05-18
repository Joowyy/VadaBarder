package com.example.vadabarder.data

import android.content.Context
import com.example.vadabarder.R
import java.util.Calendar

object BarberiaData {

    // ── Servicios ─────────────────────────────────────────────────────────
    // Clave: R.string.servicio_*  →  Valor: precio en €
    val servicios = linkedMapOf(
        R.string.servicio_corte_clasico  to 12,
        R.string.servicio_fade           to 14,
        R.string.servicio_barba          to 8,
        R.string.servicio_perfilado      to 6,
        R.string.servicio_corte_barba    to 20,
        R.string.servicio_afeitado       to 15,
        R.string.servicio_diseno_barba   to 10,
        R.string.servicio_cejas          to 5
    )

    val serviciosPopulares = listOf(
        R.string.servicio_corte_clasico,
        R.string.servicio_fade,
        R.string.servicio_barba,
        R.string.servicio_perfilado,
        R.string.servicio_corte_barba
    )

    // ── Horario ───────────────────────────────────────────────────────────
    val horarioSemana = linkedMapOf<Int, String?>(
        R.string.horario_lunes_viernes to "09:00–14:00  ·  16:00–20:00",
        R.string.horario_sabado        to "09:00–14:00",
        R.string.horario_domingo       to null
    )

    val horasMañana = listOf(
        "09:00", "09:30", "10:00", "10:30", "11:00", "11:30",
        "12:00", "12:30", "13:00", "13:30"
    )
    val horasTarde = listOf(
        "16:00", "16:30", "17:00", "17:30", "18:00", "18:30",
        "19:00", "19:30"
    )

    fun horasPorDia(diaSemana: Int): List<String>? = when (diaSemana) {
        Calendar.SUNDAY   -> null
        Calendar.SATURDAY -> horasMañana
        else              -> horasMañana + horasTarde
    }

    // ── Traducción de servicios guardados en Firestore ────────────────────
    //
    // Mapa inverso: cualquier forma conocida del nombre → R.string.servicio_*
    // Incluye ES (datos históricos), EN, FR y los entry-names canónicos
    // (formato nuevo que usa AddFragment al guardar).
    private val NOMBRE_A_RESID: Map<String, Int> = mapOf(
        // ── Español ──────────────────────────────────────────────────────
        "Corte clásico"         to R.string.servicio_corte_clasico,
        "Fade / Degradado"      to R.string.servicio_fade,
        "Arreglo de barba"      to R.string.servicio_barba,
        "Perfilado"             to R.string.servicio_perfilado,
        "Corte + Barba"         to R.string.servicio_corte_barba,
        "Afeitado navaja"       to R.string.servicio_afeitado,
        "Diseño de barba"       to R.string.servicio_diseno_barba,
        "Cejas"                 to R.string.servicio_cejas,
        // ── English ──────────────────────────────────────────────────────
        "Classic Cut"           to R.string.servicio_corte_clasico,
        "Fade"                  to R.string.servicio_fade,
        "Beard Trim"            to R.string.servicio_barba,
        "Outline"               to R.string.servicio_perfilado,
        "Cut + Beard"           to R.string.servicio_corte_barba,
        "Straight Razor Shave"  to R.string.servicio_afeitado,
        "Beard Design"          to R.string.servicio_diseno_barba,
        "Eyebrow Trim"          to R.string.servicio_cejas,
        // ── Français ─────────────────────────────────────────────────────
        "Coupe classique"       to R.string.servicio_corte_clasico,
        "Fondu / Dégradé"       to R.string.servicio_fade,
        "Taille de barbe"       to R.string.servicio_barba,
        "Contour"               to R.string.servicio_perfilado,
        "Coupe + Barbe"         to R.string.servicio_corte_barba,
        "Rasage au rasoir"      to R.string.servicio_afeitado,
        "Design de barbe"       to R.string.servicio_diseno_barba,
        "Épilation sourcils"    to R.string.servicio_cejas,
        // ── Entry-names canónicos (formato nuevo, separador "||") ─────────
        "servicio_corte_clasico"  to R.string.servicio_corte_clasico,
        "servicio_fade"           to R.string.servicio_fade,
        "servicio_barba"          to R.string.servicio_barba,
        "servicio_perfilado"      to R.string.servicio_perfilado,
        "servicio_corte_barba"    to R.string.servicio_corte_barba,
        "servicio_afeitado"       to R.string.servicio_afeitado,
        "servicio_diseno_barba"   to R.string.servicio_diseno_barba,
        "servicio_cejas"          to R.string.servicio_cejas
    )

    fun resolverServicio(context: Context, servicio: String): String {
        val trimmed = servicio.trim()

        // 1. Coincidencia exacta (nombres simples o compuestos conocidos)
        NOMBRE_A_RESID[trimmed]?.let { return context.getString(it) }

        // 2. Formato nuevo: entry-names separados por "||"
        if (trimmed.contains("||")) {
            return trimmed.split("||").joinToString(" + ") { parte ->
                val resId = NOMBRE_A_RESID[parte.trim()]
                if (resId != null) context.getString(resId) else parte.trim()
            }
        }

        // 3. Formato antiguo: nombres localizados separados por " + "
        return trimmed.split(" + ").joinToString(" + ") { parte ->
            val resId = NOMBRE_A_RESID[parte.trim()]
            if (resId != null) context.getString(resId) else parte.trim()
        }
    }
}
