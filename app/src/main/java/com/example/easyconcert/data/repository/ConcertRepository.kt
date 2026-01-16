package com.example.easyconcert.data.repository

import com.example.easyconcert.data.model.Concert
import com.example.easyconcert.data.model.Order
import com.example.easyconcert.data.model.WebResponse
import com.example.easyconcert.data.remote.ApiService

class ConcertRepository(private val apiService: ApiService) {

    // 1. Ambil Semua (Untuk List di Home/Validasi) - SUDAH OKE
    suspend fun getConcerts(): WebResponse<List<Concert>> {
        return apiService.getConcerts()
    }

    // 2. 👇 TAMBAHAN WAJIB: Ambil Satu Konser (Untuk Pre-fill Form Edit)
    suspend fun getConcertById(id: Int): WebResponse<Concert> {
        return apiService.getConcertById(id)
    }

    // 3. Tambah Konser (Ubah parameter jadi Concert object)
    suspend fun addConcert(concert: Concert): WebResponse<Concert> {
        return apiService.addConcert(concert)
    }

    // 4. Update Konser (Ubah parameter jadi Concert object)
    suspend fun updateConcert(id: Int, concert: Concert): WebResponse<Concert> {
        return apiService.updateConcert(id, concert)
    }

    // 5. Hapus Konser - SUDAH OKE
    suspend fun deleteConcert(id: Int): WebResponse<Concert> {
        return apiService.deleteConcert(id)
    }

    // Tambahkan fungsi ini
    suspend fun getOrders(token: String): WebResponse<List<Order>> {
        return apiService.getOrders(token)
    }
}