package com.example.easyconcert.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.easyconcert.data.model.Concert
import com.example.easyconcert.data.repository.ConcertRepository
import kotlinx.coroutines.launch

// State untuk menampung inputan form
data class InsertUiState(
    val eventName: String = "",
    val date: String = "",
    val venue: String = "",
    val artistLineup: String = "",
    val priceVip: String = "",
    val priceReguler: String = "",
    val posterImage: String = "",
    val isLoading: Boolean = false,
    val error: String? = null
)

class ManageConcertViewModel(private val repository: ConcertRepository) : ViewModel() {

    var uiState by mutableStateOf(InsertUiState())
        private set

    // Fungsi untuk mengubah inputan user saat mengetik
    fun updateUiState(newState: InsertUiState) {
        uiState = newState
    }

    // 1. FUNGSI LOAD DATA (Untuk Mode Edit)
    fun loadConcertData(concertId: Int) {
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true)
            try {
                val response = repository.getConcertById(concertId) // Pastikan repo punya fungsi ini
                // Isi form dengan data dari server
                uiState = InsertUiState(
                    eventName = response.data?.eventName ?: "",
                    date = response.data?.date ?: "",
                    venue = response.data?.venue ?: "",
                    artistLineup = response.data?.artistLineup ?: "",
                    priceVip = response.data?.priceVip.toString(),
                    priceReguler = response.data?.priceReguler.toString(),
                    posterImage = response.data?.posterImage ?: ""
                )
            } catch (e: Exception) {
                uiState = uiState.copy(isLoading = false, error = "Gagal ambil data: ${e.message}")
            }
        }
    }

    // 2. FUNGSI SIMPAN (Bisa Tambah atau Edit)
    fun saveConcert(concertId: Int?, onSuccess: () -> Unit) {
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true)
            try {
                // Siapkan objek Concert
                val concert = Concert(
                    concertId = concertId ?: 0,
                    eventName = uiState.eventName,
                    date = uiState.date,
                    venue = uiState.venue,
                    artistLineup = uiState.artistLineup,
                    priceVip = uiState.priceVip.toIntOrNull() ?: 0,
                    priceReguler = uiState.priceReguler.toIntOrNull() ?: 0,
                    posterImage = uiState.posterImage
                )

                if (concertId == null || concertId == 0) {
                    // --- MODE TAMBAH ---
                    repository.addConcert(concert)
                } else {
                    // --- MODE EDIT ---
                    repository.updateConcert(concertId, concert)
                }

                onSuccess() // Kembali ke halaman sebelumnya
            } catch (e: Exception) {
                uiState = uiState.copy(isLoading = false, error = "Gagal simpan: ${e.message}")
            }
        }
    }

    fun deleteConcert(concertId: Int, onSuccess: () -> Unit) {
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true)
            try {
                repository.deleteConcert(concertId)
                onSuccess() // Refresh list setelah hapus
            } catch (e: Exception) {
                uiState = uiState.copy(isLoading = false, error = "Gagal hapus: ${e.message}")
            }
        }
    }
}