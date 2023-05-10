package com.example.criminalintent.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.criminalintent.model.Crime
import kotlinx.coroutines.flow.Flow
import java.util.UUID

@Dao
interface CrimeDao {

    @Query("SELECT * FROM crime")
    fun getCrimes(): Flow<List<Crime>>

    @Query("SELECT * FROM crime WHERE id=(:id)")
    fun getCrime(id: UUID): Flow<Crime>

    @Insert
    suspend fun addCrimes(crimes: List<Crime>)

}