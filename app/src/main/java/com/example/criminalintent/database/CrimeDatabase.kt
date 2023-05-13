package com.example.criminalintent.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.criminalintent.model.Crime

private const val CRIME_DATABASE = "CrimeDatabase"

@Database(entities = [Crime::class], version = 3)
@TypeConverters(CrimeTypeConverters::class)
abstract class CrimeDatabase : RoomDatabase() {

    abstract fun crimeDao(): CrimeDao

    companion object {

        private var INSTANCE: CrimeDatabase? = null

        fun initialize(context: Context) {
            if (INSTANCE == null) {
                INSTANCE = Room
                    .databaseBuilder(
                        context.applicationContext,
                        CrimeDatabase::class.java,
                        CRIME_DATABASE
                    )
                    .addMigrations(migration_1_2, migration_2_3)
                    .build()
            }
        }

        fun get(): CrimeDatabase {
            return INSTANCE ?: throw IllegalStateException("CrimeDatabase must be initialized")
        }
    }
}

val migration_1_2 = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL(
            """
            ALTER TABLE $CRIME_DATABASE
            ADD COLUMN suspect
            TEXT NOT NULL
            DEFAULT ''
            """
        )
    }
}

val migration_2_3 = object : Migration(2, 3) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL(
            """
                ALTER TABLE $CRIME_DATABASE
                ADD COLUMN photoFileName
                TEXT
            """.trimIndent()
        )
    }

}