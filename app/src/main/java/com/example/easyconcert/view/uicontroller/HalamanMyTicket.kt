package com.example.easyconcert.view.uicontroller

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.ConfirmationNumber
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.easyconcert.data.UserSession
import com.example.easyconcert.data.container.AppContainer
import com.example.easyconcert.data.model.Ticket
import com.example.easyconcert.viewmodel.BookingViewModel
import com.example.easyconcert.viewmodel.provider.PenyediaViewModel
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.common.BitMatrix

@Composable
fun HalamanMyTicket(
    onHomeClick: () -> Unit,
    onProfileClick: () -> Unit,
    appContainer: AppContainer
) {
    val viewModel: BookingViewModel = viewModel(factory = PenyediaViewModel.Factory)
    val myTickets = viewModel.myTickets.collectAsState().value

    // Warna Tema
    val DarkBackground = Color(0xFF020617)
    val TextWhite = Color.White
    val AccentBlue = Color(0xFF1D4ED8)

    LaunchedEffect(Unit) {
        val token = UserSession.token
        if (token.isNotEmpty()) {
            viewModel.loadMyTickets(token)
        }
    }

    Scaffold(
        containerColor = DarkBackground,
        bottomBar = {
            NavigationBar(
                containerColor = DarkBackground,
                contentColor = TextWhite
            ) {
                NavigationBarItem(
                    selected = false,
                    onClick = onHomeClick,
                    icon = { Icon(Icons.Default.Home, null) },
                    label = { Text("Home") },
                    colors = NavigationBarItemDefaults.colors(
                        unselectedIconColor = Color.Gray,
                        unselectedTextColor = Color.Gray
                    )
                )
                NavigationBarItem(
                    selected = true,
                    onClick = {},
                    icon = { Icon(Icons.Outlined.ConfirmationNumber, null) },
                    label = { Text("Tiket") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = AccentBlue,
                        selectedTextColor = AccentBlue,
                        indicatorColor = AccentBlue.copy(alpha = 0.2f),
                        unselectedIconColor = Color.Gray,
                        unselectedTextColor = Color.Gray
                    )
                )
                NavigationBarItem(
                    selected = false,
                    onClick = onProfileClick,
                    icon = { Icon(Icons.Default.Person, null) },
                    label = { Text("Profil") },
                    colors = NavigationBarItemDefaults.colors(
                        unselectedIconColor = Color.Gray,
                        unselectedTextColor = Color.Gray
                    )
                )
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Tiket Saya",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = TextWhite,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                if (myTickets.isEmpty()) {
                    item {
                        Box(modifier = Modifier.fillMaxWidth().height(300.dp), contentAlignment = Alignment.Center) {
                            Text("Belum ada tiket aktif.", style = MaterialTheme.typography.bodyLarge, color = Color.Gray)
                        }
                    }
                } else {
                    items(myTickets) { ticket ->
                        TicketItemQr(ticket)
                    }
                }
            }
        }
    }
}

// --- ITEM TIKET DENGAN QR CODE ---
@Composable
fun TicketItemQr(ticket: Ticket) {
    val CardBackground = Color(0xFF1E293B)
    val TextWhite = Color.White

    // Generate QR Code (Ingat di-remember biar ga berat)
    val qrBitmap = remember(ticket.ticketCode) {
        generateQrCode(ticket.ticketCode)
    }

    val statusColor = if (ticket.status == "VALID") Color(0xFF22C55E) else Color(0xFFEF4444)

    Card(
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(0.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = ticket.eventName,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = TextWhite,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = ticket.date,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.LightGray
            )

            Spacer(modifier = Modifier.height(24.dp))

            // --- BOX QR CODE (Wajib Background Putih agar bisa discan) ---
            Box(
                modifier = Modifier
                    .size(200.dp) // Ukuran QR Code
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.White) // Background Putih Wajib
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                if (qrBitmap != null) {
                    Image(
                        bitmap = qrBitmap.asImageBitmap(),
                        contentDescription = "QR Code Tiket",
                        contentScale = ContentScale.FillBounds,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Text("Gagal Load QR", color = Color.Black)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Text Code di bawah QR (Backup kalau QR ga kebaca)
            Text(
                text = ticket.ticketCode,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 2.sp,
                color = TextWhite
            )

            Spacer(modifier = Modifier.height(16.dp))
            Divider(color = Color.Gray.copy(alpha = 0.3f), thickness = 1.dp)
            Spacer(modifier = Modifier.height(16.dp))

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    Text("Kategori", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                    Text(ticket.category, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold, color = TextWhite)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("Status", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                    Text(ticket.status, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = statusColor)
                }
            }
        }
    }
}

// --- FUNGSI HELPER UNTUK MEMBUAT QR CODE ---
fun generateQrCode(content: String): Bitmap? {
    return try {
        val width = 512
        val height = 512
        val bitMatrix: BitMatrix = MultiFormatWriter().encode(content, BarcodeFormat.QR_CODE, width, height)
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
        for (x in 0 until width) {
            for (y in 0 until height) {
                // Jika bit true -> Hitam, jika false -> Putih
                bitmap.setPixel(x, y, if (bitMatrix[x, y]) android.graphics.Color.BLACK else android.graphics.Color.WHITE)
            }
        }
        bitmap
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}