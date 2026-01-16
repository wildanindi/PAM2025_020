package com.example.easyconcert.viewmodel


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.easyconcert.data.model.Concert
import com.example.easyconcert.data.repository.ConcertRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class HomeUiState(
    val isLoading: Boolean = false,
    val concertList: List<Concert> = emptyList(),
    val error: String? = null
)

class HomeViewModel(private val repository: ConcertRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        getConcerts()
    }

    fun getConcerts() {
        viewModelScope.launch {
            _uiState.value = HomeUiState(isLoading = true)
            try {
                val response = repository.getConcerts()
                if (response.data != null) {
                    _uiState.value = HomeUiState(concertList = response.data)
                } else {
                    _uiState.value = HomeUiState(error = "Data kosong")
                }
            } catch (e: Exception) {
                _uiState.value = HomeUiState(error = e.message)
            }
        }
    }
//private fun getConcerts() {
//    // --- 🔴 BAGIAN DUMMY DATA KONSER (Ganti logika fetch) ---
//
//    // Kita buat daftar konser palsu
//    val dummyList = listOf(
//        Concert(
//            concertId = 1,
//            eventName = "Konser Sheila On 7",
//            date = "2025-12-31",
//            venue = "GBK Jakarta",
//            artistLineup = "Sheila On 7, Padi Reborn",
//            priceVip = 500000,
//            priceReguler = 150000,
//
//            posterImage = "https://i.pinimg.com/564x/a4/09/20/a4092063cb535c5f8dfbe98463f6057a.jpg" // Gambar random internet
//        ),
//        Concert(
//            concertId = 2,
//            eventName = "Coldplay Music of Spheres",
//            date = "2026-01-20",
//            venue = "JIS Jakarta",
//            artistLineup = "Coldplay",
//            priceVip = 2000000,
//            priceReguler = 800000,
//
//            posterImage = "https://i.pinimg.com/736x/2a/b3/30/2ab33010356e300969571c696e512c01.jpg"
//        )
//    )
//
//    // Langsung masukkan ke state tanpa loading internet
//    _uiState.value = HomeUiState(
//        isLoading = false,
//        concertList = dummyList
//    )
//
//    // --- 🔴 TUTUP/KOMENTAR KODINGAN ASLI DI BAWAH INI ---
//    /*
//    viewModelScope.launch {
//        _uiState.value = HomeUiState(isLoading = true)
//        try {
//            val response = repository.getConcerts()
//            ...
//        } catch (e: Exception) { ... }
//    }
//    */
//}
}