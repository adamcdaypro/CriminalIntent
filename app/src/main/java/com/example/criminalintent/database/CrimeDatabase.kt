package com.example.criminalintent.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.criminalintent.model.Crime

@Database(entities = [Crime::class], version = 1)
@TypeConverters(CrimeTypeConverters::class)
abstract class CrimeDatabase : RoomDatabase() {

    abstract fun crimeDao(): CrimeDao

    companion object {

        private var INSTANCE: CrimeDatabase? = null

        fun initialize(context: Context) {
            if (INSTANCE == null) {
                INSTANCE = Room.databaseBuilder(
                    context.applicationContext,
                    CrimeDatabase::class.java,
                    CrimeDatabase::class.java.toString()
                ).build()
            }
        }

        fun get(): CrimeDatabase {
            return INSTANCE ?: throw IllegalStateException("CrimeDatabase must be initialized")
        }
    }
}