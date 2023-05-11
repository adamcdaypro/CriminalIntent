package com.example.criminalintent

import com.example.criminalintent.database.CrimeDatabase
import com.example.criminalintent.model.Crime
import kotlinx.coroutines.flow.Flow
import java.util.UUID

class CrimeRepository {

    private val crimeDatabase = CrimeDatabase.get()

    fun getCrimes(): Flow<List<Crime>> = crimeDatabase.crimeDao().getCrimes()

    fun getCrime(id: UUID): Flow<Crime> = crimeDatabase.crimeDao().getCrime(id)

    suspend fun updateCrime(crime: Crime) = crimeDatabase.crimeDao().updateCrime(crime)

}
