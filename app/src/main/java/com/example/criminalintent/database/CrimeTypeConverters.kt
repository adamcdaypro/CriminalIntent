package com.example.criminalintent.database

import androidx.room.TypeConverter
import java.util.Date

class CrimeTypeConverters {

    @TypeConverter
    fun from(date: Date): Long {
        return date.time
    }

    @TypeConverter
    fun from(long: Long): Date {
        return Date(long)
    }
}