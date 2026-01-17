package com.example.easyconcert.data.model

import com.google.gson.annotations.SerializedName

data class DashboardStats(
    @SerializedName("totalSold")
    val totalSold: Int = 0,

    @SerializedName("totalScanned")
    val totalScanned: Int = 0
)