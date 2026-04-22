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

    @Query("SELECT * FROM citas ORDER BY fecha ASC, hora ASC")
    fun obtenerTodas(): LiveData<List<Cita>>
}
