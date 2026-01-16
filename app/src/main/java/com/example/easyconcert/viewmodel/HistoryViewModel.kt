package com.example.easyconcert.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.easyconcert.data.UserSession // Import UserSession yang baru dibuat
import com.example.easyconcert.data.repository.ConcertRepository
import kotlinx.coroutines.launch

// State UI (sama seperti sebelumnya)
data class HistoryUiState(
    val orderList: List<com.example.easyconcert.data.model.Order> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

// Hapus AuthRepository dari sini, kita pakai UserSession saja biar simpel
class HistoryViewModel(private val repository: ConcertRepository) : ViewModel() {

    var uiState by mutableStateOf(HistoryUiState())
        private set

    init {
        getHistory()
    }

    fun getHistory() {
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true)
            try {
                // 👇 1. AMBIL TOKEN DARI USERSESSION (Bukan AuthRepository)
                val token = UserSession.token

                // Cek log biar yakin
                android.util.Log.d("HISTORY_DEBUG", "Token yang dipakai: $token")

                // 👇 2. PANGGIL REPOSITORY DENGAN BENAR
                // Jangan tulis 'ConcertRepository.getOrders', tapi 'repository.getOrders' (huruf kecil)
                // Pastikan fungsi getOrders di ConcertRepository kamu menerima parameter string token
                val response = repository.getOrders(token)

                if (response.data != null) {
                    uiState = uiState.copy(isLoading = false, orderList = response.data)
                } else {
                    uiState = uiState.copy(isLoading = false)
                }
            } catch (e: Exception) {
                android.util.Log.e("HISTORY_DEBUG", "Error: ${e.message}")
                uiState = uiState.copy(isLoading = false, error = e.message)
            }
        }
    }
}