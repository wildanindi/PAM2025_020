package com.example.easyconcert.data.model


import com.google.gson.annotations.SerializedName

// <T> artinya Generic, bisa diisi data User, List<Concert>, atau Ticket
data class WebResponse<T>(
    @SerializedName("status")
    val status: String, // "success" atau "error"

    @SerializedName("message")
    val message: String,

    @SerializedName("data")
    val data: T? = null
)
