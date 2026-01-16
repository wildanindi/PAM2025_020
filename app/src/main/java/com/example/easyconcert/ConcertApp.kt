package com.example.easyconcert


import android.app.Application
import com.example.easyconcert.data.container.AppContainer
import com.example.easyconcert.data.container.DefaultAppContainer

class ConcertApp : Application() {

    // Container ini akan hidup selama aplikasi berjalan
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        // Inisialisasi container saat aplikasi pertama kali dibuka
        container = DefaultAppContainer()
    }
}