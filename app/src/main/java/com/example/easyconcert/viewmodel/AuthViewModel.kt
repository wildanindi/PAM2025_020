package com.example.easyconcert.viewmodel


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.easyconcert.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// State untuk menampung hasil Login
data class LoginState(
    val isLoading: Boolean = false,
    val token: String? = null,
    val role: String = "FAN", // "FAN" atau "CREW"
    val error: String? = null
)

class AuthViewModel(private val repository: AuthRepository) : ViewModel() {

    private val _loginState = MutableStateFlow(LoginState())
    val loginState: StateFlow<LoginState> = _loginState.asStateFlow()


    fun login(u: String, p: String) {
        viewModelScope.launch {
            _loginState.value = LoginState(isLoading = true)
            try {
                val response = repository.login(u, p)
                if (response.status == "success" && response.data != null) {

                    com.example.easyconcert.data.UserSession.token = response.data.token ?: ""
                    com.example.easyconcert.data.UserSession.isLoggedIn = true
                    com.example.easyconcert.data.UserSession.name = response.data.username ?: ""


                    _loginState.value = LoginState(
                        token = response.data.token,
                        role = response.data.role
                    )
                } else {
                    _loginState.value = LoginState(error = response.message)
                }
            } catch (e: Exception) {
                _loginState.value = LoginState(error = "Gagal koneksi: ${e.message}")
            }
        }
    }

    fun register(fullName: String, user: String, pass: String, role: String) {
        viewModelScope.launch {
            _loginState.value = LoginState(isLoading = true)
            try {
                // 2. Bungkus data ke dalam Map agar jadi JSON yang rapi
                // Kunci (sebelah kiri) HARUS SAMA dengan yang diminta Node.js
                val registerData = mapOf(
                    "full_name" to fullName,
                    "username" to user,
                    "password" to pass,
                    "role" to role
                )

                // 3. Panggil Repository dengan mengirim Map tersebut
                val response = repository.register(registerData)

                if (response.status == "success") {
                    _loginState.value = LoginState(
                        token = "REGISTER_SUCCESS", // Penanda sukses sementara
                        role = role
                    )
                } else {
                    _loginState.value = LoginState(error = response.message)
                }
            } catch (e: Exception) {
                _loginState.value = LoginState(error = "Gagal Daftar: ${e.message}")
            }
        }
    }
}
//fun login(u: String, p: String) {
//    // --- 🔴 BAGIAN CHEAT / BYPASS (Tambahkan Ini) ---
//    if (u == "admin" && p == "123") {
//        _loginState.value = LoginState(
//            token = "TOKEN_PALSU_12345", // Token asal saja
//            role = "FAN" // Bisa ganti "CREW" kalau mau tes halaman Crew
//        )
//        return // Stop, jangan lanjut ke network call di bawah
//    }
//    // ------------------------------------------------
//
//    // Logika asli (Koneksi Server) tetap biarkan di bawahnya
//    // Jadi kalau bukan "admin", dia tetap coba connect internet
//    viewModelScope.launch {
//        _loginState.value = LoginState(isLoading = true)
//        try {
//            val response = repository.login(u, p)
//                if (response.status == "success" && response.data != null) {
//                    _loginState.value = LoginState(
//                        token = response.data.token,
//                        role = response.data.role
//                    )
//                } else {
//                    _loginState.value = LoginState(error = response.message)
//                }
//            // ... kodingan asli ...
//        } catch (e: Exception) {
//            _loginState.value = LoginState(error = "Gagal: ${e.message}")
//        }
//    }
//}
//}