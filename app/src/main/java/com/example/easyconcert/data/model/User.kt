package com.example.easyconcert.data.model


import com.google.gson.annotations.SerializedName

data class User(
    @SerializedName("user_id")
    val userId: Int,

    @SerializedName("username")
    val username: String,

    @SerializedName("full_name")
    val fullName: String,

    //@SerializedName("email")
    //val email: String,

    @SerializedName("role")
    val role: String = "FAN", // Nilai: "FAN" atau "CREW"

    // Token JWT (Biasanya didapat saat login untuk otentikasi request selanjutnya)
    @SerializedName("token")
    val token: String? = null
)
