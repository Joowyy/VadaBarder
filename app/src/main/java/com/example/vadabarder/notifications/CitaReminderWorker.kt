package com.example.vadabarder.notifications

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.vadabarder.R
import com.example.vadabarder.utils.LocaleHelper

class CitaReminderWorker(ctx: Context, params: WorkerParameters) : Worker(ctx, params) {

    override fun doWork(): Result {
        val servicio = inputData.getString("servicio") ?: return Result.success()
        val fecha    = inputData.getString("fecha")    ?: return Result.success()
        val hora     = inputData.getString("hora")     ?: return Result.success()
        val tipo     = inputData.getString("tipo")     ?: "1h"

        // Obtener contexto con el locale guardado por el usuario
        val ctx = LocaleHelper.wrap(applicationContext)

        val titulo  = if (tipo == "24h") ctx.getString(R.string.notif_titulo_24h)
                      else               ctx.getString(R.string.notif_titulo_1h)
        val mensaje = ctx.getString(R.string.notif_mensaje, servicio, fecha, hora)
        val notifId = "$fecha$hora$tipo".hashCode()

        NotificationHelper.enviarNotificacion(applicationContext, titulo, mensaje, notifId)
        return Result.success()
    }
}
