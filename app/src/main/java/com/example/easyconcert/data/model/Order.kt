package com.example.easyconcert.data.model

import com.google.gson.annotations.SerializedName

data class Order(
    @SerializedName("order_id")
    val orderId: Int = 0,

    @SerializedName("trans_date")
    val transDate: String = "", // Tanggal transaksi

    @SerializedName("total_price")
    val totalPrice: String = "0",

    @SerializedName("payment_status")
    val paymentStatus: String = "PENDING", // PAID atau PENDING

    @SerializedName("concertName")
    val concertName: String = "", // Opsional, jika nanti di-join query
)