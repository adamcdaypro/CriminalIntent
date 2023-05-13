package com.example.criminalintent

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.criminalintent.model.Crime
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Date
import java.util.UUID

class CrimeDetailViewModel @OptIn(DelicateCoroutinesApi::class) constructor(
    private val id: UUID,
    private val globalCoroutineScope: CoroutineScope = GlobalScope
) : ViewModel() {

    private val crimeRepository: CrimeRepository = CrimeRepository()

    private val _crime: MutableStateFlow<Crime?> = MutableStateFlow(null)
    val crime: StateFlow<Crime?> = _crime.asStateFlow()

    init {
        viewModelScope.launch {
            crimeRepository.getCrime(id).collect { crime ->
                _crime.value = crime
            }
        }
    }

    fun updateCrimeTitle(title: String) {
        _crime.update { it?.copy(title = title) }
    }

    fun updateCrimeDate(date: Date) {
        _crime.update { it?.copy(date = date) }
    }

    fun updateCrimeSolved(isSolved: Boolean) {
        _crime.update { it?.copy(isSolved = isSolved) }
    }

    fun updateSuspect(suspect: String) {
        _crime.update { it?.copy(suspect = suspect) }
    }

    fun updatePhotoFileName(photoFileName: String?) {
        _crime.update { if (photoFileName != null) it?.copy(photoFileName = photoFileName) else it }
    }

    override fun onCleared() {
        super.onCleared()
        globalCoroutineScope.launch { crime.value?.let { crimeRepository.updateCrime(it) } }
    }

}

class CrimeDetailViewModelFactory(private val id: UUID) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return CrimeDetailViewModel(id) as T
    }
}