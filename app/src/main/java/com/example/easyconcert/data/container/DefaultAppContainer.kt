package com.example.easyconcert.data.container



import com.example.easyconcert.data.remote.ApiConfig
import com.example.easyconcert.data.repository.AuthRepository
import com.example.easyconcert.data.repository.ConcertRepository
import com.example.easyconcert.data.repository.TicketRepository

class DefaultAppContainer : AppContainer {

    // 1. Buat ApiService sekali saja (Lazy)
    // Ini akan memanggil Retrofit yang sudah kita setting IP-nya
    private val apiService by lazy {
        ApiConfig.getApiService()
    }

    // 2. Inisialisasi AuthRepository
    override val authRepository: AuthRepository by lazy {
        AuthRepository(apiService)
    }

    // 3. Inisialisasi ConcertRepository (PENTING untuk CRUD Konser)
    override val concertRepository: ConcertRepository by lazy {
        ConcertRepository(apiService)
    }

    // 4. Inisialisasi TicketRepository
    override val ticketRepository: TicketRepository by lazy {
        TicketRepository(apiService)
    }
}