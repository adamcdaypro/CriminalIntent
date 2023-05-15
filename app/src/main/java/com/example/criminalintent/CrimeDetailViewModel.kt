package com.example.criminalintent

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.criminalintent.filemanager.PhotoFileManager
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

const val STATE_NEXT_PHOTO_FILE_NAME = "nextPhotoFileName"

class CrimeDetailViewModel @OptIn(DelicateCoroutinesApi::class) constructor(
    private val id: UUID,
    private val savedStateHandle: SavedStateHandle = SavedStateHandle(),
    private val globalCoroutineScope: CoroutineScope = GlobalScope,
) : ViewModel() {

    private val crimeRepository: CrimeRepository = CrimeRepository()

    private val _crime: MutableStateFlow<Crime?> = MutableStateFlow(null)
    val crime: StateFlow<Crime?> = _crime.asStateFlow()

    private var nextPhotoFileName: String? = null

    init {
        viewModelScope.launch {
            crimeRepository.getCrime(id).collect { crime ->
                _crime.value = crime
            }
        }
        nextPhotoFileName = savedStateHandle[STATE_NEXT_PHOTO_FILE_NAME]
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

    fun setNextPhotoFileName(photoFileName: String) {
        nextPhotoFileName = photoFileName
    }

    fun updatePhotoFileName(update: Boolean, context: Context) {
        if (update) _crime.update {
            maybeDeleteOldPhoto(context)
            if (nextPhotoFileName != null) it?.copy(photoFileName = nextPhotoFileName) else it
        }
    }

    private fun maybeDeleteOldPhoto(context: Context) {
        PhotoFileManager.deletePhoto(crime.value?.photoFileName ?: "", context)
    }

    override fun onCleared() {
        super.onCleared()
        savedStateHandle[STATE_NEXT_PHOTO_FILE_NAME] = nextPhotoFileName
        globalCoroutineScope.launch { crime.value?.let { crimeRepository.updateCrime(it) } }
    }

}

class CrimeDetailViewModelFactory(private val id: UUID) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return CrimeDetailViewModel(id) as T
    }
}