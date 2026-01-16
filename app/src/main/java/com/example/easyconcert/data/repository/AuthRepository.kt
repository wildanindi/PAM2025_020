package com.example.easyconcert.data.repository

import com.example.easyconcert.data.model.User
import com.example.easyconcert.data.model.WebResponse
import com.example.easyconcert.data.remote.ApiService

class AuthRepository(private val apiService: ApiService) {

    // Fungsi Login
    suspend fun login(u: String, p: String): WebResponse<User> {
        // ⚠️ INI KUNCINYA! Harus sama persis dengan req.body di Node.js
        val loginData = mapOf(
            "username" to u,  // Jangan diganti jadi "user"
            "password" to p   // Jangan diganti jadi "pass"
        )
        return apiService.login(loginData)
    }

    // Fungsi Register
    suspend fun register(registerData: Map<String, String>): WebResponse<User> {
        return apiService.register(registerData)
    }
}