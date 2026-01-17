package com.example.easyconcert.viewmodel


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.easyconcert.data.model.Ticket
import com.example.easyconcert.data.repository.TicketRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class ValidationState(
    val isValid: Boolean = false,
    val message: String? = null,
    val ticketData: Ticket? = null
)

class ValidationViewModel(private val repository: TicketRepository) : ViewModel() {

    private val _validationState = MutableStateFlow(ValidationState())
    val validationState: StateFlow<ValidationState> = _validationState

    // Token hardcode dulu atau ambil dari Session Manager nanti
    // Di real app, token ini didapat dari LoginState
    private val crewToken = "DUMMY_TOKEN_CREW"

    fun validateTicket(token: String, code: String) {
        viewModelScope.launch {
            _validationState.value = ValidationState() // Tambahkan loading biar UX bagus
            try {
                // 1. Perbaikan: Pakai parameter 'token', bukan 'crewToken'
                val response = repository.validateTicket(token, code)

                if (response.status == "success" && response.data != null) {

                    // 2. Perbaikan Logika:
                    // Server Node.js kita mengirim pesan:
                    // - Sukses: "TIKET VALID! SILAKAN MASUK."
                    // - Gagal: "TIKET SUDAH TERPAKAI (USED)"

                    // Jadi, kita cek saja pesannya mengandung kata "VALID" atau tidak
                    val isTiketValid = response.message.contains("VALID", ignoreCase = true)

                    _validationState.value = ValidationState(
                        isValid = isTiketValid,
                        message = response.message // Tampilkan langsung pesan dari server
                    )
                } else {
                    // Ini untuk kasus status='error' (misal tiket tidak ditemukan)
                    _validationState.value = ValidationState(
                        isValid = false,
                        message = response.message
                    )
                }
            } catch (e: Exception) {
                _validationState.value = ValidationState(
                    isValid = false,
                    message = "Error Koneksi: ${e.message}"
                )
            }
        }
    }
}


