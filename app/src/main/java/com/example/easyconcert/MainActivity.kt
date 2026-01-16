package com.example.easyconcert

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
// import androidx.activity.enableEdgeToEdge // Opsional, bisa di-comment jika UI tertutup status bar
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.example.easyconcert.ui.theme.EasyConcertTheme
import com.example.easyconcert.view.route.ConcertNavGraph

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // enableEdgeToEdge()

        setContent {
            EasyConcertTheme {
                // Surface: Memastikan background aplikasi mengikuti tema (Putih/Hitam)
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // 1. Ambil Kunci Gudang (Container) dari ConcertApp
                    // Ini penting agar Repository bisa dipakai di semua halaman
                    val appContainer = (application as ConcertApp).container

                    // 2. Siapkan Pengendali Navigasi
                    val navController = rememberNavController()

                    // 3. Panggil Peta Navigasi Utama (NavGraph)
                    // Halaman pertama (Login) akan muncul dari sini
                    ConcertNavGraph(
                        navController = navController,
                        appContainer = appContainer
                    )
                }
            }
        }
    }
}