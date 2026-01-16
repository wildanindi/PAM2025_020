package com.example.easyconcert.data.model


import com.google.gson.annotations.SerializedName

data class Concert(
    @SerializedName("concert_id")
    val concertId: Int,

    @SerializedName("event_name")
    val eventName: String,

    @SerializedName("date")
    val date: String, // Format string "YYYY-MM-DD"

    @SerializedName("venue")
    val venue: String,

    @SerializedName("artist_lineup")
    val artistLineup: String,

    // Harga sesuai RAT (DECIMAL), di Kotlin kita pakai Double atau Int
    @SerializedName("price_vip")
    val priceVip: Int,

    @SerializedName("price_reguler")
    val priceReguler: Int,

    // Tambahan untuk UI (biasanya API kirim URL gambar poster)
    @SerializedName("poster_image")
    val posterImage: String? = null
)
