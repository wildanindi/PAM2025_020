package com.example.easyconcert.view.uicontroller

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.easyconcert.data.container.AppContainer
import com.example.easyconcert.viewmodel.HomeViewModel
import com.example.easyconcert.viewmodel.provider.PenyediaViewModel

@Composable
fun HalamanDetail(
    concertId: Int,
    onBuyClick: (Int, String, Int) -> Unit, // Kirim ID, Kategori, Qty
    onBackClick: () -> Unit,
    appContainer: AppContainer
) {
    val viewModel: HomeViewModel = viewModel(factory = PenyediaViewModel.Factory)
    // Cari konser berdasarkan ID (Logika tetap sama)
    val concert = viewModel.uiState.collectAsState().value.concertList.find { it.concertId == concertId }

    // State Lokal
    var selectedCategory by remember { mutableStateOf("REGULER") }
    var quantity by remember { mutableIntStateOf(1) }

    // --- WARNA TEMA DARK ---
    val DarkBackground = Color(0xFF020617)
    val CardBackground = Color(0xFF1E293B)
    val AccentBlue = Color(0xFF1D4ED8)
    val TextWhite = Color.White
    val TextGray = Color.LightGray

    if (concert != null) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(DarkBackground)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()) // Agar bisa discroll
                    .padding(bottom = 80.dp) // Ruang untuk tombol bawah
            ) {
                // 1. POSTER BESAR
                Box {
                    AsyncImage(
                        model = concert.posterImage,
                        contentDescription = null,
                        modifier = Modifier
                            .height(300.dp)
                            .fillMaxWidth(),
                        contentScale = ContentScale.Crop
                    )
                    // Tombol Back Overlay
                    IconButton(
                        onClick = onBackClick,
                        modifier = Modifier
                            .padding(top = 48.dp, start = 16.dp)
                            .clip(CircleShape)
                            .background(Color.Black.copy(alpha = 0.5f))
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = TextWhite
                        )
                    }
                }

                Column(modifier = Modifier.padding(24.dp)) {
                    // 2. JUDUL & INFO
                    Text(
                        text = concert.eventName,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextWhite
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "📍 ${concert.venue}",
                        style = MaterialTheme.typography.bodyLarge,
                        color = TextGray
                    )
                    Text(
                        text = "🎤 ${concert.artistLineup}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextGray
                    )

                    Spacer(modifier = Modifier.height(24.dp))
                    Divider(color = Color.Gray.copy(alpha = 0.3f))
                    Spacer(modifier = Modifier.height(24.dp))

                    // 3. PILIHAN KATEGORI (Card Style)
                    Text(
                        text = "Pilih Tiket",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = TextWhite
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    // Card REGULER
                    TicketCategoryCard(
                        title = "REGULER",
                        price = "Rp ${concert.priceReguler}",
                        isSelected = selectedCategory == "REGULER",
                        onClick = { selectedCategory = "REGULER" },
                        colorTag = Color(0xFF22C55E) // Hijau
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Card VIP
                    TicketCategoryCard(
                        title = "VIP",
                        price = "Rp ${concert.priceVip}",
                        isSelected = selectedCategory == "VIP",
                        onClick = { selectedCategory = "VIP" },
                        colorTag = Color(0xFFEAB308) // Kuning Emas
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    // 4. JUMLAH TIKET
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(CardBackground, RoundedCornerShape(12.dp))
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "Jumlah Tiket",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = TextWhite
                        )

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            // Tombol Kurang
                            IconButton(
                                onClick = { if (quantity > 1) quantity-- },
                                modifier = Modifier
                                    .size(36.dp)
                                    .background(Color.DarkGray, CircleShape)
                            ) {
                                Icon(Icons.Default.Remove, null, tint = TextWhite)
                            }

                            Text(
                                text = "$quantity",
                                modifier = Modifier.padding(horizontal = 20.dp),
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = TextWhite
                            )

                            // Tombol Tambah
                            IconButton(
                                onClick = { quantity++ },
                                modifier = Modifier
                                    .size(36.dp)
                                    .background(AccentBlue, CircleShape)
                            ) {
                                Icon(Icons.Default.Add, null, tint = TextWhite)
                            }
                        }
                    }
                }
            }

            // 5. TOMBOL BAYAR (Sticky Bottom)
            Button(
                onClick = { onBuyClick(concertId, selectedCategory, quantity) },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(24.dp)
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AccentBlue)
            ) {
                Text(
                    text = "LANJUT BAYAR",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextWhite
                )
            }
        }
    } else {
        // Loading State Gelap
        Box(
            modifier = Modifier.fillMaxSize().background(DarkBackground),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = AccentBlue)
        }
    }
}

// --- KOMPONEN KARTU KATEGORI TIKET ---
@Composable
fun TicketCategoryCard(
    title: String,
    price: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    colorTag: Color
) {
    val CardBackground = Color(0xFF1E293B)
    val SelectedBorder = Color(0xFF1D4ED8) // Biru border kalau dipilih

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        border = if (isSelected) androidx.compose.foundation.BorderStroke(2.dp, SelectedBorder) else null,
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                // Tag Kategori (REG/VIP)
                Surface(
                    color = colorTag.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = title,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = colorTag
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                // Garis Dummy (Hiasan)
                Box(
                    modifier = Modifier
                        .width(100.dp)
                        .height(6.dp)
                        .background(Color.Gray.copy(alpha = 0.3f), RoundedCornerShape(2.dp))
                )
            }
            // Harga
            Text(
                text = price,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF3B82F6) // Biru Muda
            )
        }
    }
}