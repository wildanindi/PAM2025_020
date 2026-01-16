package com.example.easyconcert.view.uicontroller

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.easyconcert.data.model.Order
import com.example.easyconcert.viewmodel.HistoryViewModel
import com.example.easyconcert.viewmodel.provider.PenyediaViewModel

@Composable
fun HalamanHistory(
    onBackClick: () -> Unit
) {
    val viewModel: HistoryViewModel = viewModel(factory = PenyediaViewModel.Factory)
    val uiState = viewModel.uiState

    // --- WARNA TEMA DARK ---
    val DarkBackground = Color(0xFF020617)
    val TextWhite = Color.White
    val AccentBlue = Color(0xFF1D4ED8)

    Scaffold(
        containerColor = DarkBackground // Background Halaman Gelap
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(horizontal = 24.dp)
        ) {

            // 1. HEADER CUSTOM (Mirip Checkout)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(top = 24.dp, bottom = 24.dp)
            ) {
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.1f)) // Tombol transparan
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Kembali",
                        tint = TextWhite
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = "Riwayat Pesanan",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = TextWhite
                )
            }

            // 2. CONTENT STATE
            if (uiState.isLoading) {
                // Loading Biru
                LinearProgressIndicator(
                    modifier = Modifier.fillMaxWidth(),
                    color = AccentBlue,
                    trackColor = Color.DarkGray
                )
            }

            if (uiState.orderList.isEmpty() && !uiState.isLoading) {
                // Tampilan Kosong
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Belum ada pesanan",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.Gray
                        )
                    }
                }
            } else {
                // List Order Gelap
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(bottom = 24.dp)
                ) {
                    items(uiState.orderList) { order ->
                        OrderCardDark(order)
                    }
                }
            }
        }
    }
}

@Composable
fun OrderCardDark(order: Order) {
    // Definisi Warna Lokal
    val CardBackground = Color(0xFF1E293B)
    val TextWhite = Color.White
    val TextGray = Color.LightGray
    val AccentBlue = Color(0xFF3B82F6) // Biru agak terang untuk harga

    // Warna Status
    val isPaid = order.paymentStatus == "PAID"
    val statusBg = if (isPaid) Color(0xFF064E3B) else Color(0xFF451A03) // Background status (Hijau Tua / Coklat Tua)
    val statusText = if (isPaid) Color(0xFF34D399) else Color(0xFFFB923C) // Teks Status (Hijau Muda / Orange Muda)

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp).fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Nama Event (Truncate kalau kepanjangan)
                Text(
                    text = order.concertName ?: "Event",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextWhite,
                    modifier = Modifier.weight(1f)
                )

                Spacer(modifier = Modifier.width(8.dp))

                // Badge Status Modern
                Surface(
                    color = statusBg,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = order.paymentStatus,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        color = statusText,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Detail Info
            Column {
                Text(
                    text = "Order ID: #${order.orderId}",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextGray
                )
                Text(
                    text = "Tanggal: ${order.transDate.take(10)}", // Ambil YYYY-MM-DD saja
                    style = MaterialTheme.typography.bodySmall,
                    color = TextGray
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
            Divider(color = Color.Gray.copy(alpha = 0.2f))
            Spacer(modifier = Modifier.height(16.dp))

            // Total Harga
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Total Bayar",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextWhite
                )
                Text(
                    text = "Rp ${order.totalPrice ?: "0"}",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = AccentBlue
                )
            }
        }
    }
}