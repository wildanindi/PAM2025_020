package com.example.easyconcert.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.easyconcert.data.model.DashboardStats
import com.example.easyconcert.data.model.Ticket
import com.example.easyconcert.data.model.ValidationRequest // ✅ Pastikan ada file model ini
import com.example.easyconcert.data.repository.TicketRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class ValidationState(
    val isLoading: Boolean = false, // ✅ Tambahan untuk indikator loading
    val isValid: Boolean = false,
    val message: String? = null,
    val ticketData: Ticket? = null, // ✅ Agar data tiket (Event Name) muncul
    val stats: DashboardStats = DashboardStats(0, 0)
)

class ValidationViewModel(private val repository: TicketRepository) : ViewModel() {

    private val _validationState = MutableStateFlow(ValidationState())
    val validationState: StateFlow<ValidationState> = _validationState

    fun validateTicket(token: String, code: String) {
        viewModelScope.launch {
            // 1. Set Loading, TAPI pertahankan stats lama agar tidak kedip 0
            _validationState.value = _validationState.value.copy(
                isLoading = true,
                message = null
            )

            try {
                // Bungkus kode tiket ke object Request (sesuai format JSON Body)
                val request = ValidationRequest(ticketCode = code)

                val response = repository.validateTicket(token, code)

                if (response.status == "success") {
                    // Logic Cek Pesan dari Server
                    val isTiketValid = response.message.contains("VALID", ignoreCase = true)

                    _validationState.value = ValidationState(
                        isLoading = false,
                        isValid = isTiketValid,
                        message = response.message,
                        ticketData = response.data, // Simpan data tiket

                        // Pertahankan stats lama sementara
                        stats = _validationState.value.stats
                    )

                    // 👇👇👇 PERBAIKAN UTAMA DI SINI 👇👇👇
                    // Panggil update statistik HANYA jika validasi sukses.
                    // Ini menjamin data di server sudah berubah jadi 'USED' sebelum kita hitung ulang.
                    loadDashboardStats(token)

                } else {
                    // Gagal dari Server (Misal: Kode tidak ditemukan)
                    _validationState.value = ValidationState(
                        isLoading = false,
                        isValid = false,
                        message = response.message,
                        stats = _validationState.value.stats // Pertahankan stats lama
                    )
                }
            } catch (e: Exception) {
                // Error Koneksi / Exception
                _validationState.value = ValidationState(
                    isLoading = false,
                    isValid = false,
                    message = "Error Koneksi: ${e.message}",
                    stats = _validationState.value.stats // Pertahankan stats lama
                )
            }
        }
    }

    fun loadDashboardStats(token: String) {
        viewModelScope.launch {
            try {
                val response = repository.getDashboardStats(token)
                if (response.status == "success") {
                    // Update HANYA bagian stats, sisanya biarkan (menggunakan copy)
                    _validationState.value = _validationState.value.copy(
                        stats = response.data ?: DashboardStats(0, 0)
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
                // Jika gagal load stats, biarkan angka sebelumnya (tidak perlu reset ke 0)
            }
        }
    }
}