package com.example.easyconcert.view.uicontroller

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.easyconcert.data.UserSession // Import UserSession
import com.example.easyconcert.data.container.AppContainer
import com.example.easyconcert.viewmodel.BookingViewModel
import com.example.easyconcert.viewmodel.HomeViewModel
import com.example.easyconcert.viewmodel.provider.PenyediaViewModel

@Composable
fun HalamanCheckout(
    concertId: Int,
    category: String,
    quantity: Int,
    onPaymentSuccess: () -> Unit,
    onBackClick: () -> Unit,
    appContainer: AppContainer
) {
    // ViewModel
    val homeViewModel: HomeViewModel = viewModel(factory = PenyediaViewModel.Factory)
    val bookingViewModel: BookingViewModel = viewModel(factory = PenyediaViewModel.Factory)

    // Cari Data Konser
    val concert = homeViewModel.uiState.collectAsState().value.concertList.find { it.concertId == concertId }

    // Hitung Total
    val pricePerItem = if (category == "VIP") concert?.priceVip ?: 0 else concert?.priceReguler ?: 0
    val totalPrice = pricePerItem * quantity

    // Status Booking
    val bookingStatus = bookingViewModel.bookingStatus.collectAsState().value

    // --- WARNA TEMA DARK ---
    val DarkBackground = Color(0xFF020617)
    val CardBackground = Color(0xFF1E293B)
    val AccentBlue = Color(0xFF1D4ED8)
    val TextWhite = Color.White
    val TextGray = Color.Gray

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
        ) {
            // 1. HEADER (Back Button & Title)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(top = 16.dp, bottom = 32.dp)
            ) {
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.1f))
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = TextWhite
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = "Order Summary",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = TextWhite
                )
            }

            if (concert != null) {
                // 2. KARTU RINCIAN BIAYA (Sesuai Gambar Referensi Bagian Atas)
                Card(
                    colors = CardDefaults.cardColors(containerColor = CardBackground),
                    shape = RoundedCornerShape(24.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(24.dp)) {
                        // Baris 1: Harga Satuan
                        RowSummary(
                            label = "Harga Tiket ($category)",
                            value = "Rp $pricePerItem",
                            textColor = TextWhite,
                            valueColor = TextWhite
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        // Baris 2: Jumlah
                        RowSummary(
                            label = "Jumlah Pesanan",
                            value = "$quantity Tiket",
                            textColor = TextWhite,
                            valueColor = TextWhite
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        // Baris 3: Biaya Layanan (Dummy agar mirip gambar)
                        RowSummary(
                            label = "Biaya Layanan",
                            value = "Rp 0", // Bisa diubah kalau ada logic pajak
                            textColor = TextGray,
                            valueColor = TextGray
                        )

                        Spacer(modifier = Modifier.height(16.dp))
                        Divider(color = Color.Gray.copy(alpha = 0.2f))
                        Spacer(modifier = Modifier.height(16.dp))

                        // Baris 4: TOTAL
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "TOTAL",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = TextWhite
                            )
                            Text(
                                text = "Rp $totalPrice",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = AccentBlue // Biru Mencolok
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // 3. KARTU DETAIL EVENT (Mengisi kotak kosong di gambar referensi)
                Card(
                    colors = CardDefaults.cardColors(containerColor = CardBackground.copy(alpha = 0.5f)),
                    shape = RoundedCornerShape(24.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(24.dp)) {
                        Text(
                            text = "Detail Event",
                            style = MaterialTheme.typography.labelLarge,
                            color = TextGray
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = concert.eventName,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = TextWhite
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = concert.date,
                            color = TextGray
                        )
                        Text(
                            text = concert.venue,
                            color = TextGray
                        )
                    }
                }
            }
        }

        // 4. BAGIAN BAWAH (Pesan Status & Tombol)
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(24.dp)
                .fillMaxWidth()
        ) {
            // Tampilkan Pesan Sukses/Gagal jika ada
            if (bookingStatus != null) {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = if (bookingStatus.contains("Berhasil")) Color(0xFF064E3B) else Color(0xFF7F1D1D)
                    ),
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
                ) {
                    Text(
                        text = bookingStatus,
                        color = TextWhite,
                        modifier = Modifier.padding(16.dp),
                        fontWeight = FontWeight.Bold,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }

                if (bookingStatus.contains("Berhasil")) {
                    Button(
                        onClick = onPaymentSuccess,
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = AccentBlue)
                    ) {
                        Text("LIHAT TIKET SAYA", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                }
            } else {
                // Tombol Bayar Normal
                Button(
                    onClick = {
                        // 👇 PERBAIKAN: Gunakan Token Asli dari UserSession
                        val token = UserSession.token
                        bookingViewModel.buyTicket(token, concertId, category, quantity)
                    },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AccentBlue)
                ) {
                    Text("Buy Ticket", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

// Helper untuk Baris Rincian Biaya
@Composable
fun RowSummary(label: String, value: String, textColor: Color, valueColor: Color) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, style = MaterialTheme.typography.bodyMedium, color = textColor)
        Text(text = value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold, color = valueColor)
    }
}