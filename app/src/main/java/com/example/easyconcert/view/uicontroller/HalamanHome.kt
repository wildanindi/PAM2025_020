package com.example.easyconcert.view.uicontroller

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
// 👇 Ini import ikon Tiket yang benar (Butuh library extended)
import androidx.compose.material.icons.outlined.ConfirmationNumber
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.easyconcert.data.container.AppContainer
import com.example.easyconcert.data.model.Concert
import com.example.easyconcert.viewmodel.HomeViewModel
import com.example.easyconcert.viewmodel.provider.PenyediaViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HalamanHome(
    onConcertClick: (Int) -> Unit,
    onMyTicketClick: () -> Unit,
    onProfileClick: () -> Unit,
    appContainer: AppContainer
) {
    val viewModel: HomeViewModel = viewModel(factory = PenyediaViewModel.Factory)
    val uiState = viewModel.uiState.collectAsState().value

    // --- WARNA TEMA (Di dalam fungsi agar aman) ---
    val DarkBackground = Color(0xFF020617)
    val SearchBarColor = Color(0xFFD9D9D9)
    val AccentBlue = Color(0xFF1D4ED8)
    val TextWhite = Color.White

    // State Search
    var searchQuery by remember { mutableStateOf("") }

    Scaffold(
        containerColor = DarkBackground,
        bottomBar = {
            NavigationBar(
                containerColor = DarkBackground,
                contentColor = TextWhite
            ) {
                // HOME
                NavigationBarItem(
                    selected = true,
                    onClick = {},
                    icon = { Icon(Icons.Default.Home, null) },
                    label = { Text("Home") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = AccentBlue,
                        selectedTextColor = AccentBlue,
                        indicatorColor = AccentBlue.copy(alpha = 0.2f),
                        unselectedIconColor = Color.Gray,
                        unselectedTextColor = Color.Gray
                    )
                )
                // TIKET (Pakai ConfirmationNumber)
                NavigationBarItem(
                    selected = false,
                    onClick = onMyTicketClick,
                    // 👇 Ikon Tiket Asli
                    icon = { Icon(Icons.Outlined.ConfirmationNumber, null) },
                    label = { Text("Tiket") },
                    colors = NavigationBarItemDefaults.colors(
                        unselectedIconColor = Color.Gray,
                        unselectedTextColor = Color.Gray
                    )
                )
                // PROFILE
                NavigationBarItem(
                    selected = false,
                    onClick = onProfileClick,
                    icon = { Icon(Icons.Default.Person, null) },
                    label = { Text("Profile") },
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

            // SEARCH BAR
            TextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Cari nama konser...", color = Color.Gray) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color.Black) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = SearchBarColor,
                    unfocusedContainerColor = SearchBarColor,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    cursorColor = Color.Black,
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(24.dp))

            // LOGIKA FILTER
            val filteredConcerts = if (searchQuery.isBlank()) {
                uiState.concertList
            } else {
                uiState.concertList.filter { concert ->
                    concert.eventName.contains(searchQuery, ignoreCase = true)
                }
            }

            // LIST KONSER
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                if (filteredConcerts.isEmpty() && searchQuery.isNotBlank()) {
                    item {
                        Text(
                            text = "Konser tidak ditemukan :(",
                            modifier = Modifier.padding(top = 16.dp),
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color(0xFFFF5252)
                        )
                    }
                }

                items(filteredConcerts) { concert ->
                    ConcertCardDark(concert = concert, onClick = { onConcertClick(concert.concertId) })
                }
            }
        }
    }
}

// Komponen Card Versi Dark Mode
@Composable
fun ConcertCardDark(concert: Concert, onClick: () -> Unit) {
    val CardBackground = Color(0xFF1E293B)
    val TextWhite = Color.White
    val AccentBlue = Color(0xFF1D4ED8)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column {
            AsyncImage(
                model = concert.posterImage,
                contentDescription = null,
                modifier = Modifier
                    .height(180.dp)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)),
                contentScale = ContentScale.Crop
            )
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = concert.eventName,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = TextWhite
                )
                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = concert.date,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.LightGray
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Mulai Rp ${concert.priceReguler}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = AccentBlue
                )
            }
        }
    }
}