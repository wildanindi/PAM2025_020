package com.example.easyconcert.data.repository


import com.example.easyconcert.data.model.BookingRequest
import com.example.easyconcert.data.model.DashboardStats
import com.example.easyconcert.data.model.Ticket
import com.example.easyconcert.data.model.ValidationRequest
import com.example.easyconcert.data.model.WebResponse
import com.example.easyconcert.data.remote.ApiService

class TicketRepository(private val apiService: ApiService) {

    // 1. Fungsi Beli Tiket (FANS)
    suspend fun buyTicket(
        token: String,
        concertId: Int,
        category: String,
        quantity: Int
    ): WebResponse<List<Ticket>> {

        // Buat object request body sesuai model
        val request = BookingRequest(
            concertId = concertId,
            category = category,
            quantity = quantity
        )

        // Kirim token & request ke API
        // Perhatikan: biasanya token butuh prefix "Bearer "
        return apiService.buyTicket("Bearer $token", request)
    }

    // 2. Fungsi Lihat Tiket Saya (FANS)
    suspend fun getMyTickets(token: String): WebResponse<List<Ticket>> {
        return apiService.getMyTickets("Bearer $token")
    }

    // 3. Fungsi Validasi Tiket (CREW)
    suspend fun validateTicket(token: String, ticketCode: String): WebResponse<Ticket> {

        // Buat object request body
        val request = ValidationRequest(ticketCode = ticketCode)

        return apiService.validateTicket("Bearer $token", request)
    }

    suspend fun getDashboardStats(token: String): WebResponse<DashboardStats> {
        return apiService.getDashboardStats("Bearer $token")
    }

}