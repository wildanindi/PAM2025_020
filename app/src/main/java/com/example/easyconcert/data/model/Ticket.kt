package com.example.easyconcert.data.model


import com.google.gson.annotations.SerializedName

// 1. Model Tiket (Yang dilihat user di menu 'My Ticket')
// Sesuai tabel 'tickets' di RAT
data class Ticket(
    val ticketCode: String, // Primary Key (UUID String)

    val eventName: String, // Dari relasi ke table concerts

    @SerializedName("date")
    val date: String,

    @SerializedName("venue")
    val venue: String,

    @SerializedName("owner_name")
    val ownerName: String,

    @SerializedName("category")
    val category: String, // "VIP" atau "REGULER"

    @SerializedName("status")
    val status: String, // "VALID" atau "USED"

    val posterImage: String? = null
)

// 2. Model Request Booking (Data yang dikirim saat user klik 'Bayar')
// Sesuai alur 'Input Order' di Flow User.png
data class BookingRequest(
    @SerializedName("concert_id")
    val concertId: Int,

    @SerializedName("category")
    val category: String, // User pilih VIP/Reguler

    @SerializedName("quantity")
    val quantity: Int
)

// 3. Model Request Validasi (Data yang dikirim Crew saat input kode)
data class ValidationRequest(
    @SerializedName("ticketCode")
    val ticketCode: String
)
