package com.example.easyconcert.view.uicontroller

import android.Manifest
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material.icons.filled.QrCodeScanner // Ikon Scanner
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
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.easyconcert.data.UserSession
import com.example.easyconcert.data.model.Concert
import com.example.easyconcert.view.component.CameraPreview // ✅ Pastikan file ini sudah dibuat
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
    val context = LocalContext.current

    // State Logic
    var ticketCode by remember { mutableStateOf("") }
    val validationState = validationViewModel.validationState.collectAsState().value
    val concertListState = viewModel.uiState.collectAsState().value

    // State Dialog Hapus & Scanner
    var showDeleteDialog by remember { mutableStateOf(false) }
    var concertToDelete by remember { mutableStateOf<Concert?>(null) }
    var showCameraScanner by remember { mutableStateOf(false) } // State untuk kamera

    // --- DEFINISI WARNA TEMA DARK ---
    val DarkBackground = Color(0xFF020617)
    val CardBackground = Color(0xFF1E293B)
    val TextWhite = Color.White
    val AccentBlue = Color(0xFF1D4ED8)
    val SuccessGreen = Color(0xFF22C55E)
    val ErrorRed = Color(0xFFEF4444)

    // --- 1. CONFIG PERMISSION KAMERA ---
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            showCameraScanner = true // Jika diizinkan, buka kamera
        } else {
            Toast.makeText(context, "Izin kamera diperlukan untuk scan tiket", Toast.LENGTH_SHORT).show()
        }
    }

    // --- FUNGSI HELPER: CEK TIKET ---
    fun performCheck(code: String = ticketCode) {
        val token = UserSession.token
        if (token.isNotEmpty()) {
            // Jika code kosong (misal tombol manual ditekan tapi kosong), jangan kirim
            if (code.isNotEmpty()) {
                validationViewModel.validateTicket(token, code)
            } else {
                Toast.makeText(context, "Masukkan kode tiket dulu", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(context, "Sesi habis, silakan login ulang.", Toast.LENGTH_SHORT).show()
        }
    }

    Scaffold(
        containerColor = DarkBackground
    ) { padding ->

        // --- 2. DIALOG FULLSCREEN KAMERA (Muncul jika showCameraScanner = true) ---
        if (showCameraScanner) {
            Dialog(
                onDismissRequest = { showCameraScanner = false },
                properties = DialogProperties(usePlatformDefaultWidth = false) // Full Screen Mode
            ) {
                Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
                    // Panggil Komponen CameraPreview (Pastikan file CameraPreview.kt sudah ada)
                    CameraPreview(
                        onBarcodeDetected = { scannedCode ->
                            // 3. AUTO FILL & CHECK LOGIC
                            ticketCode = scannedCode      // Isi kolom input
                            showCameraScanner = false     // Tutup kamera
                            performCheck(scannedCode)     // Langsung validasi ke server
                        }
                    )

                    // Tombol Tutup (X)
                    IconButton(
                        onClick = { showCameraScanner = false },
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(16.dp)
                            .background(Color.Black.copy(alpha = 0.5f), shape = RoundedCornerShape(50))
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Tutup", tint = Color.White)
                    }

                    // Teks Petunjuk
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 40.dp)
                            .background(Color.Black.copy(alpha = 0.6f), RoundedCornerShape(8.dp))
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Text("Arahkan kamera ke QR Code", color = Color.White)
                    }
                }
            }
        }

        // DIALOG HAPUS DATA
        if (showDeleteDialog && concertToDelete != null) {
            AlertDialog(
                onDismissRequest = { showDeleteDialog = false },
                containerColor = CardBackground,
                icon = { Icon(Icons.Default.Warning, contentDescription = null, tint = ErrorRed) },
                title = { Text(text = "Hapus Konser?", color = TextWhite, fontWeight = FontWeight.Bold) },
                text = { Text(text = "Anda yakin ingin menghapus konser '${concertToDelete?.eventName}'?", color = Color.LightGray) },
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
            modifier = Modifier.padding(padding).fillMaxSize().padding(horizontal = 24.dp),
            contentPadding = PaddingValues(bottom = 32.dp)
        ) {
            // HEADER
            item {
                Spacer(modifier = Modifier.height(24.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Column {
                        Text("Dashboard Crew", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = TextWhite)
                        Text("Manage & Validate", style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
                    }
                    IconButton(
                        onClick = onLogoutClick,
                        modifier = Modifier.clip(RoundedCornerShape(12.dp)).background(Color.Red.copy(alpha = 0.2f))
                    ) { Icon(Icons.AutoMirrored.Filled.ExitToApp, "Logout", tint = ErrorRed) }
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

                        // LAYOUT BARU: Input Text + Tombol Scanner Berdampingan
                        Row(verticalAlignment = Alignment.CenterVertically) {

                            // Input Manual
                            OutlinedTextField(
                                value = ticketCode,
                                onValueChange = { ticketCode = it },
                                label = { Text("Kode Tiket") },
                                modifier = Modifier.weight(1f),
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
                                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                                keyboardActions = KeyboardActions(onDone = { performCheck() })
                            )

                            Spacer(modifier = Modifier.width(8.dp))

                            // TOMBOL SCANNER BARU 📸
                            FilledIconButton(
                                onClick = {
                                    // Minta izin kamera dulu
                                    permissionLauncher.launch(Manifest.permission.CAMERA)
                                },
                                modifier = Modifier.size(56.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = IconButtonDefaults.filledIconButtonColors(containerColor = AccentBlue)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.QrCodeScanner,
                                    contentDescription = "Scan QR",
                                    tint = Color.White
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = { performCheck() },
                            modifier = Modifier.fillMaxWidth().height(50.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = AccentBlue)
                        ) {
                            Text("CEK STATUS MANUAL", fontWeight = FontWeight.Bold)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
            }

            // HASIL VALIDASI
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
                        Row(modifier = Modifier.padding(16.dp).fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                            Icon(iconStatus, null, tint = statusColor, modifier = Modifier.size(32.dp))
                            Spacer(modifier = Modifier.width(16.dp))
                            Column {
                                Text(titleStatus, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = statusColor)
                                Text(validationState.message ?: "", style = MaterialTheme.typography.bodySmall, color = TextWhite)
                                // Jika ada data tiket tambahan, tampilkan di sini
                                validationState.ticketData?.let { t ->
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text("Event: ${t.eventName ?: "-"}", color = Color.Gray, style = MaterialTheme.typography.bodySmall)
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }

            // HEADER DAFTAR KONSER
            item {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
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

            // LIST KONSER
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

// Komponen Kartu Admin (Tetap Sama)
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
        Row(modifier = Modifier.padding(12.dp).fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
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