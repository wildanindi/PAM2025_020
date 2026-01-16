package com.example.easyconcert.data


// Ini adalah "Dompet" global untuk menyimpan token sementara
object UserSession {
    var token: String = ""
    var isLoggedIn: Boolean = false

    var name: String = ""
}