package com.example.easyconcert.view.uicontroller

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.ConfirmationNumber // Ikon Tiket
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.easyconcert.data.UserSession // Import UserSession
import com.example.easyconcert.data.container.AppContainer

@Composable
fun HalamanProfile(
    onLogout: () -> Unit,
    onHomeClick: () -> Unit,
    onMyTicketClick: () -> Unit,
    onHistoryClick: () -> Unit,
    appContainer: AppContainer
) {
    // --- WARNA TEMA DARK MODE ---
    val DarkBackground = Color(0xFF020617)
    val CardBackground = Color(0xFF1E293B)
    val AccentBlue = Color(0xFF1D4ED8)
    val TextWhite = Color.White
    val RedDanger = Color(0xFFEF4444) // Warna Merah untuk Logout

    Scaffold(
        containerColor = DarkBackground, // Background Gelap
        bottomBar = {
            NavigationBar(
                containerColor = DarkBackground,
                contentColor = TextWhite
            ) {
                // HOME
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
                // TIKET
                NavigationBarItem(
                    selected = false,
                    onClick = onMyTicketClick,
                    icon = { Icon(Icons.Outlined.ConfirmationNumber, null) },
                    label = { Text("Tiket") },
                    colors = NavigationBarItemDefaults.colors(
                        unselectedIconColor = Color.Gray,
                        unselectedTextColor = Color.Gray
                    )
                )
                // PROFILE (Aktif)
                NavigationBarItem(
                    selected = true,
                    onClick = {},
                    icon = { Icon(Icons.Default.Person, null) },
                    label = { Text("Profil") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = AccentBlue,
                        selectedTextColor = AccentBlue,
                        indicatorColor = AccentBlue.copy(alpha = 0.2f),
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
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            // HEADER TITLE
            Text(
                text = "My Profile",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = TextWhite
            )

            Spacer(modifier = Modifier.height(32.dp))

            // AVATAR USER
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .background(CardBackground, shape = CircleShape) // Lingkaran Luar
                    .padding(2.dp)
                    .background(DarkBackground, shape = CircleShape), // Gap
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    tint = TextWhite,
                    modifier = Modifier.size(60.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // INFO USER
            // Menampilkan nama asli jika ada di UserSession, default "User Penggemar"
            val displayName = if (UserSession.name.isNotEmpty()) UserSession.name else "User Penggemar"

            Text(
                text = displayName,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = TextWhite
            )
            Text(
                text = "FAN MEMBER",
                style = MaterialTheme.typography.bodyMedium,
                color = AccentBlue,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(40.dp))

            // --- MENU CARD ---
            Card(
                colors = CardDefaults.cardColors(containerColor = CardBackground),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {

                    // Menu 1: Riwayat Pesanan
                    ProfileMenuItemDark(
                        text = "Riwayat Pesanan",
                        onClick = onHistoryClick,
                        textColor = TextWhite
                    )

                    Divider(color = Color.Gray.copy(alpha = 0.2f))

                    // Menu 2: Edit Profile (Dummy)
                    ProfileMenuItemDark(
                        text = "Edit Profile",
                        onClick = {},
                        textColor = TextWhite
                    )

                    Divider(color = Color.Gray.copy(alpha = 0.2f))

                    // Menu 3: Bantuan (Dummy)
                    ProfileMenuItemDark(
                        text = "Pusat Bantuan",
                        onClick = {},
                        textColor = TextWhite
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // TOMBOL LOGOUT (Merah)
            Button(
                onClick = onLogout,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = RedDanger)
            ) {
                Text("LOGOUT", color = Color.White, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

// --- KOMPONEN MENU ITEM DARK ---
@Composable
fun ProfileMenuItemDark(
    text: String,
    onClick: () -> Unit,
    textColor: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text, style = MaterialTheme.typography.bodyLarge, color = textColor)
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
            contentDescription = null,
            tint = Color.Gray
        )
    }
}