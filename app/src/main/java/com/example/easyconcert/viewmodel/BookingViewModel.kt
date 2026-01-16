package com.example.easyconcert.viewmodel


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.easyconcert.data.model.Ticket
import com.example.easyconcert.data.repository.TicketRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class BookingViewModel(private val repository: TicketRepository) : ViewModel() {

    // State untuk status pembelian
    private val _bookingStatus = MutableStateFlow<String?>(null)
    val bookingStatus: StateFlow<String?> = _bookingStatus

    // State untuk list tiket saya
    private val _myTickets = MutableStateFlow<List<Ticket>>(emptyList())
    val myTickets: StateFlow<List<Ticket>> = _myTickets

     //Fungsi Beli Tiket
    fun buyTicket(token: String, concertId: Int, category: String, qty: Int) {
        viewModelScope.launch {
            try {
                val response = repository.buyTicket(token, concertId, category, qty)
                if (response.status == "success") {
                    _bookingStatus.value = "Berhasil! Tiket sudah masuk menu MyTicket."
                } else {
                    _bookingStatus.value = "Gagal: ${response.message}"
                }
            } catch (e: Exception) {
                _bookingStatus.value = "Error: ${e.message}"
            }
        }
    }

    // Fungsi Load Tiket Saya
    fun loadMyTickets(token: String) {
        viewModelScope.launch {
            try {
                val response = repository.getMyTickets(token)
                if (response.data != null) {
                    _myTickets.value = response.data
                }
            } catch (e: Exception) {
                // Handle error silent atau tampilkan snackbar
            }
        }
    }

//    fun buyTicket(token: String, concertId: Int, category: String, qty: Int) {
//        // --- 🔴 BYPASS SUKSES BAYAR ---
//        _bookingStatus.value = "Berhasil! (Mode Offline)"
//
//        // Kalau mau lebih canggih, tambahkan tiket palsu ke list myTickets sekalian
//        val fakeTicket = Ticket(
//            ticketCode = "VIP-${(100..999).random()}", // Kode acak
//            eventName = "Konser Dummy",
//            date = "2025-01-01",
//            venue = "Lokasi Palsu",
//            ownerName = "User Admin",
//            category = category,
//            status = "VALID"
//        )
//        // Tambahkan ke list yang sudah ada
//        _myTickets.value = _myTickets.value + fakeTicket
//
//        return // Stop, jangan jalankan kodingan internet di bawah
//        // -----------------------------
//
//        /* Kodingan Asli Internet viewModelScope.launch { ... } */
//    }
//
//    // Bypass Load My Ticket
//    fun loadMyTickets(token: String) {
//        // Biarkan kosong atau isi dummy jika mau,
//        // tapi logika buyTicket di atas sudah nambahin tiket otomatis ke memory.
//    }
}