package com.example.vadabarder.notifications

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters

class CitaReminderWorker(ctx: Context, params: WorkerParameters) : Worker(ctx, params) {

    override fun doWork(): Result {
        val servicio = inputData.getString("servicio") ?: return Result.success()
        val fecha    = inputData.getString("fecha")    ?: return Result.success()
        val hora     = inputData.getString("hora")     ?: return Result.success()
        val tipo     = inputData.getString("tipo")     ?: "1h"

        val titulo  = if (tipo == "24h") "¡Cita mañana!" else "Tu cita empieza en 1 hora"
        val mensaje = "$servicio · $fecha a las $hora"
        val notifId = "$fecha$hora$tipo".hashCode()

        NotificationHelper.enviarNotificacion(applicationContext, titulo, mensaje, notifId)
        return Result.success()
    }
}
