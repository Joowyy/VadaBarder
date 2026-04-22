package com.example.vadabarder.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.vadabarder.data.dao.CitaDao
import com.example.vadabarder.data.model.Cita

@Database(entities = [Cita::class], version = 1)
abstract class VadaBarberDatabase : RoomDatabase() {

    abstract fun citaDao(): CitaDao

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
