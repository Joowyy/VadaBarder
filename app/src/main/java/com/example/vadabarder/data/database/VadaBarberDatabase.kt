package com.example.vadabarder.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.vadabarder.data.dao.CitaDao
import com.example.vadabarder.data.dao.UsuarioDao
import com.example.vadabarder.data.model.Cita
import com.example.vadabarder.data.model.Usuario

@Database(entities = [Cita::class, Usuario::class], version = 2)
abstract class VadaBarberDatabase : RoomDatabase() {

    abstract fun citaDao(): CitaDao
    abstract fun usuarioDao(): UsuarioDao

    companion object {
        @Volatile
        private var INSTANCE: VadaBarberDatabase? = null

        fun getInstance(context: Context): VadaBarberDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    VadaBarberDatabase::class.java,
                    "vadabarber.db"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
