package com.example.easyconcert.data.remote

import com.example.easyconcert.data.model.*
import retrofit2.http.*

interface ApiService {

    // --- 1. AUTHENTICATION ---
    @POST("login")
    suspend fun login(@Body user: Map<String, String>): WebResponse<User>

    @POST("register")
    suspend fun register(@Body user: Map<String, String>): WebResponse<User>


    // --- 2. FITUR FANS & MANAGE KONSER ---

    @GET("concerts/{id}")
    suspend fun getConcertById(@Path("id") id: Int): WebResponse<Concert> // ✅ Sudah benar

    @GET("concerts")
    suspend fun getConcerts(): WebResponse<List<Concert>> // ✅ Sudah benar

    // 👇 UBAH INI: Ganti Map<String, String> jadi Concert
    // Agar harga tiket terkirim sebagai Angka (Int), bukan String.
    @POST("concerts")
    suspend fun addConcert(
        @Body concert: Concert
    ): WebResponse<Concert> // Return Concert biar kita bisa cek datanya kalau perlu

    // 👇 UBAH INI JUGA: Ganti Map<String, String> jadi Concert
    @PUT("concerts/{id}")
    suspend fun updateConcert(
        @Path("id") id: Int,
        @Body concert: Concert
    ): WebResponse<Concert>

    @DELETE("concerts/{id}")
    suspend fun deleteConcert(@Path("id") id: Int): WebResponse<Concert>

    // --- TRANSAKSI ---
    @POST("tickets/buy")
    suspend fun buyTicket(
        @Header("Authorization") token: String,
        @Body request: BookingRequest
    ): WebResponse<List<Ticket>>

    @GET("tickets/my-ticket")
    suspend fun getMyTickets(
        @Header("Authorization") token: String
    ): WebResponse<List<Ticket>>

    // Tambahkan di bagian Transaksi
    @GET("orders")
    suspend fun getOrders(
        @Header("Authorization") token: String
    ): WebResponse<List<Order>>


    // --- 3. FITUR CREW ---
    @POST("tickets/validate")
    suspend fun validateTicket(
        @Header("Authorization") token: String,
        @Body request: ValidationRequest
    ): WebResponse<Ticket>
}