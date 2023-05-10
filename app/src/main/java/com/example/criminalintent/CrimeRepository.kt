package com.example.criminalintent

import com.example.criminalintent.database.CrimeDatabase
import com.example.criminalintent.model.Crime
import kotlinx.coroutines.flow.Flow

class CrimeRepository {

    private val crimeDatabase = CrimeDatabase.get()

    suspend fun addCrimes(crimes: List<Crime>) = crimeDatabase.crimeDao().addCrimes(crimes)

    fun getCrimes(): Flow<List<Crime>> = crimeDatabase.crimeDao().getCrimes()

}
