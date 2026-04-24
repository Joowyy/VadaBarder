package com.example.vadabarder.data.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.vadabarder.data.model.Cita

@Dao
interface CitaDao {

    @Insert
    fun insertar(cita: Cita)

    @Delete
    fun eliminar(cita: Cita)

    // Solo devuelve las citas del usuario activo; Room actualiza el LiveData automáticamente
    @Query("SELECT * FROM citas WHERE userId = :userId ORDER BY fecha ASC, hora ASC")
    fun obtenerPorUsuario(userId: String): LiveData<List<Cita>>
}
