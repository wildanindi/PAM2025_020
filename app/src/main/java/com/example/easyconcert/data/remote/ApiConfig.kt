package com.example.easyconcert.data.remote


import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiConfig {


    private const val BASE_URL = "http://10.0.2.2:3000/"

    fun getApiService(): ApiService {
        // 1. Setup Logging (Agar request/response muncul di Logcat Android Studio)
        val loggingInterceptor = HttpLoggingInterceptor().setLevel(
            HttpLoggingInterceptor.Level.BODY
        )

        // 2. Setup Client (Mesin koneksi)
        val client = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .build()

        // 3. Setup Retrofit (Penghubung Code ke API)
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()

        return retrofit.create(ApiService::class.java)
    }
}