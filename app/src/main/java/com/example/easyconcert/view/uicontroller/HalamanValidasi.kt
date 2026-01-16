package com.example.easyconcert.view.uicontroller

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.easyconcert.data.UserSession // ✅ Pastikan Import ini ada
import com.example.easyconcert.data.model.Concert
import com.example.easyconcert.viewmodel.HomeViewModel
import com.example.easyconcert.viewmodel.ManageConcertViewModel
import com.example.easyconcert.viewmodel.ValidationViewModel
import com.example.easyconcert.viewmodel.provider.PenyediaViewModel

@Composable
fun HalamanValidasi(
    viewModel: HomeViewModel,
    onAddConcertClick: () -> Unit,
    onEditConcertClick: (Int) -> Unit,
    onLogoutClick: () -> Unit
) {
    val validationViewModel: ValidationViewModel = viewModel(factory = PenyediaViewModel.Factory)
    val manageViewModel: ManageConcertViewModel = viewModel(factory = PenyediaViewModel.Factory)
    val context = LocalContext.current // ✅ Untuk menampilkan Toast

    // State Logic
    var ticketCode by remember { mutableStateOf("") }
    val validationState = validationViewModel.validationState.collectAsState().value
    val concertListState = viewModel.uiState.collectAsState().value

    // State Dialog Hapus
    var showDeleteDialog by remember { mutableStateOf(false) }
    var concertToDelete by remember { mutableStateOf<Concert?>(null) }

    // --- DEFINISI WARNA TEMA DARK ---
    val DarkBackground = Color(0xFF020617)
    val CardBackground = Color(0xFF1E293B)
    val TextWhite = Color.White
    val AccentBlue = Color(0xFF1D4ED8)
    val SuccessGreen = Color(0xFF22C55E)
    val ErrorRed = Color(0xFFEF4444)

    // --- FUNGSI HELPER: CEK TIKET ---
    fun performCheck() {
        val token = UserSession.token // ✅ Ambil Token Asli dari Login
        if (token.isNotEmpty()) {
            validationViewModel.validateTicket(token, ticketCode)
        } else {
            Toast.makeText(context, "Sesi habis, silakan login ulang.", Toast.LENGTH_SHORT).show()
        }
    }

    Scaffold(
        containerColor = DarkBackground
    ) { padding ->

        // KOMPONEN ALERT DIALOG (Hapus Data)
        if (showDeleteDialog && concertToDelete != null) {
            AlertDialog(
                onDismissRequest = { showDeleteDialog = false },
                containerColor = CardBackground,
                icon = { Icon(Icons.Default.Warning, contentDescription = null, tint = ErrorRed) },
                title = { Text(text = "Hapus Konser?", color = TextWhite, fontWeight = FontWeight.Bold) },
                text = {
                    Text(
                        text = "Anda yakin ingin menghapus konser '${concertToDelete?.eventName}'? Data yang dihapus tidak dapat dikembalikan.",
                        color = Color.LightGray
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            concertToDelete?.let { concert ->
                                manageViewModel.deleteConcert(concert.concertId, onSuccess = {
                                    viewModel.getConcerts()
                                })
                            }
                            showDeleteDialog = false
                            concertToDelete = null
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = ErrorRed)
                    ) { Text("HAPUS", fontWeight = FontWeight.Bold) }
                },
                dismissButton = {
                    TextButton(onClick = { showDeleteDialog = false }) { Text("Batal", color = TextWhite) }
                }
            )
        }

        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            contentPadding = PaddingValues(bottom = 32.dp)
        ) {

            // --- HEADER ---
            item {
                Spacer(modifier = Modifier.height(24.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Dashboard Crew", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = TextWhite)
                        Text("Manage & Validate", style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
                    }
                    IconButton(
                        onClick = onLogoutClick,
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.Red.copy(alpha = 0.2f))
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ExitToApp, "Logout", tint = ErrorRed)
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
            }

            // --- KARTU VALIDASI TIKET ---
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = CardBackground),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text("Validasi Tiket", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = TextWhite)
                        Spacer(modifier = Modifier.height(16.dp))

                        OutlinedTextField(
                            value = ticketCode,
                            onValueChange = { ticketCode = it },
                            label = { Text("Kode Tiket (ex: VIP-88X)") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = AccentBlue,
                                unfocusedBorderColor = Color.Gray,
                                focusedTextColor = TextWhite,
                                unfocusedTextColor = TextWhite,
                                focusedLabelColor = AccentBlue,
                                unfocusedLabelColor = Color.Gray,
                                cursorColor = AccentBlue
                            ),
                            singleLine = true,
                            // ✅ FITUR BARU: Tekan Enter di keyboard langsung cek
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                            keyboardActions = KeyboardActions(
                                onDone = { performCheck() }
                            )
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = { performCheck() }, // ✅ Panggil fungsi helper
                            modifier = Modifier.fillMaxWidth().height(50.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = AccentBlue)
                        ) {
                            Text("CEK TIKET", fontWeight = FontWeight.Bold)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
            }

            // --- HASIL VALIDASI ---
            if (validationState.message != null) {
                item {
                    val isValid = validationState.isValid
                    val statusColor = if (isValid) SuccessGreen else ErrorRed
                    val iconStatus = if (isValid) Icons.Default.CheckCircle else Icons.Default.Close
                    val titleStatus = if (isValid) "TIKET VALID" else "INVALID / GAGAL"

                    Card(
                        colors = CardDefaults.cardColors(containerColor = statusColor.copy(alpha = 0.15f)),
                        border = androidx.compose.foundation.BorderStroke(1.dp, statusColor),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp).fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(iconStatus, null, tint = statusColor, modifier = Modifier.size(32.dp))
                            Spacer(modifier = Modifier.width(16.dp))
                            Column {
                                Text(titleStatus, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = statusColor)
                                Text(validationState.message ?: "", style = MaterialTheme.typography.bodySmall, color = TextWhite)
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }

            // --- DAFTAR KONSER ---
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Daftar Konser", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = TextWhite)
                    Button(
                        onClick = onAddConcertClick,
                        colors = ButtonDefaults.buttonColors(containerColor = CardBackground),
                        contentPadding = PaddingValues(horizontal = 12.dp)
                    ) {
                        Icon(Icons.Default.Add, null, tint = TextWhite, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Tambah", color = TextWhite)
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // --- LIST ITEM KONSER ---
            items(concertListState.concertList) { concert ->
                AdminConcertCardDark(
                    concert = concert,
                    onEditClick = { onEditConcertClick(concert.concertId) },
                    onDeleteClick = {
                        concertToDelete = concert
                        showDeleteDialog = true
                    }
                )
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

@Composable
fun AdminConcertCardDark(
    concert: Concert,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    val CardBackground = Color(0xFF1E293B)
    val TextWhite = Color.White

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = concert.posterImage,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.size(60.dp).clip(RoundedCornerShape(8.dp)).background(Color.Gray)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(concert.eventName, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = TextWhite, maxLines = 1)
                Text(concert.date, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
            }
            Row {
                IconButton(onClick = onEditClick) { Icon(Icons.Default.Edit, "Edit", tint = Color(0xFF3B82F6)) }
                IconButton(onClick = onDeleteClick) { Icon(Icons.Default.Delete, "Delete", tint = Color(0xFFEF4444)) }
            }
        }
    }
}