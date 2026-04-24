package com.example.vadabarder.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.vadabarder.data.model.Usuario

@Dao
interface UsuarioDao {

    // REPLACE por si se vuelve a registrar el mismo uid (no debería pasar con UUID)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertar(usuario: Usuario)

    // Usada en el login: busca por nombre de usuario y devuelve null si no existe
    @Query("SELECT * FROM usuarios WHERE nombre = :nombre LIMIT 1")
    fun buscarPorNombre(nombre: String): Usuario?
}
