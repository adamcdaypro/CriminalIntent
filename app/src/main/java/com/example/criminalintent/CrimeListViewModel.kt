package com.example.criminalintent

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.criminalintent.model.Crime
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Date
import java.util.UUID

class CrimeListViewModel : ViewModel() {

    private val crimeRepository = CrimeRepository()

    private val _crimes: MutableStateFlow<List<Crime>> = MutableStateFlow(emptyList())
    val crimes: StateFlow<List<Crime>> = _crimes.asStateFlow()

    init {
        viewModelScope.launch {
            crimeRepository.getCrimes().collect { crimes ->
                _crimes.value = crimes
            }
        }
    }

    fun addCrime(): UUID {
        val newCrime = Crime(
            id = UUID.randomUUID(),
            title = "",
            date = Date(),
            isSolved = false
        )
        viewModelScope.launch {
            crimeRepository.addCrime(newCrime)
        }
        return newCrime.id
    }

    fun deleteCrime(id: UUID) {
        val crime = crimes.value.first { crime -> id == crime.id }
        viewModelScope.launch {
            crimeRepository.deleteCrime(crime)
        }
    }

}