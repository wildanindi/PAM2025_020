package com.example.easyconcert.view.uicontroller

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.easyconcert.viewmodel.ManageConcertViewModel
import com.example.easyconcert.viewmodel.provider.PenyediaViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HalamanFormKonser(
    concertId: Int?,
    onSuccess: () -> Unit,
    onCancel: () -> Unit
) {
    val viewModel: ManageConcertViewModel = viewModel(factory = PenyediaViewModel.Factory)
    val uiState = viewModel.uiState
    val scrollState = rememberScrollState()

    // --- WARNA TEMA DARK ---
    val DarkBackground = Color(0xFF020617)
    val CardBackground = Color(0xFF1E293B)
    val TextWhite = Color.White
    val AccentBlue = Color(0xFF1D4ED8)
    val WarningOrange = Color(0xFFF59E0B) // Warna untuk dialog konfirmasi edit

    // --- STATE VALIDASI ERROR ---
    var nameError by remember { mutableStateOf(false) }
    var dateError by remember { mutableStateOf(false) }
    var venueError by remember { mutableStateOf(false) }
    var lineupError by remember { mutableStateOf(false) }
    var priceRegError by remember { mutableStateOf(false) }
    var priceVipError by remember { mutableStateOf(false) }

    // --- STATE DIALOG KONFIRMASI (BARU) ---
    var showConfirmationDialog by remember { mutableStateOf(false) }

    // --- LOGIKA LOAD DATA ---
    LaunchedEffect(concertId) {
        if (concertId != null && concertId != 0) {
            viewModel.loadConcertData(concertId)
        }
    }

    Scaffold(
        containerColor = DarkBackground,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (concertId == 0 || concertId == null) "Tambah Konser" else "Edit Konser",
                        color = TextWhite,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onCancel) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Batal", tint = TextWhite)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = DarkBackground,
                    titleContentColor = TextWhite
                )
            )
        }
    ) { padding ->

        // 👇 1. POPUP KONFIRMASI SIMPAN/EDIT
        if (showConfirmationDialog) {
            AlertDialog(
                onDismissRequest = { showConfirmationDialog = false },
                containerColor = CardBackground,
                icon = {
                    // Ikon Info (Biru) untuk Tambah, Ikon Warning (Orange) untuk Edit
                    val isEdit = (concertId != null && concertId != 0)
                    Icon(
                        imageVector = if (isEdit) Icons.Default.Warning else Icons.Default.Info,
                        contentDescription = null,
                        tint = if (isEdit) WarningOrange else AccentBlue
                    )
                },
                title = {
                    Text(text = "Konfirmasi", color = TextWhite, fontWeight = FontWeight.Bold)
                },
                text = {
                    // Teks sesuai request Anda
                    val textMessage = if (concertId != null && concertId != 0) {
                        "Yakin ingin mengubah data?"
                    } else {
                        "Yakin ingin menyimpan data baru ini?"
                    }
                    Text(text = textMessage, color = Color.LightGray)
                },
                confirmButton = {
                    Button(
                        onClick = {
                            // PROSES SIMPAN SEBENARNYA TERJADI DI SINI
                            viewModel.saveConcert(concertId, onSuccess)
                            showConfirmationDialog = false
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (concertId != null && concertId != 0) WarningOrange else AccentBlue
                        )
                    ) {
                        Text("YA, SIMPAN", fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showConfirmationDialog = false }) {
                        Text("Batal", color = TextWhite)
                    }
                }
            )
        }

        Column(
            modifier = Modifier
                .padding(padding)
                .padding(horizontal = 24.dp)
                .verticalScroll(scrollState)
                .fillMaxSize()
        ) {

            if (uiState.isLoading) {
                LinearProgressIndicator(
                    modifier = Modifier.fillMaxWidth(),
                    color = AccentBlue,
                    trackColor = CardBackground
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            // --- FORM INPUT ---
            Card(
                colors = CardDefaults.cardColors(containerColor = CardBackground),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {

                    // 1. Nama
                    DarkTextField(
                        value = uiState.eventName,
                        onValueChange = {
                            viewModel.updateUiState(uiState.copy(eventName = it))
                            nameError = false
                        },
                        label = "Nama Konser",
                        isError = nameError,
                        errorMessage = "Nama konser wajib diisi"
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    // 2. Tanggal
                    DarkDatePickerField(
                        value = uiState.date,
                        onValueChange = { selectedDate ->
                            viewModel.updateUiState(uiState.copy(date = selectedDate))
                            dateError = false
                        },
                        label = "Tanggal (YYYY-MM-DD)",
                        isError = dateError,
                        errorMessage = "Tanggal wajib dipilih"
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    // 3. Venue
                    DarkTextField(
                        value = uiState.venue,
                        onValueChange = {
                            viewModel.updateUiState(uiState.copy(venue = it))
                            venueError = false
                        },
                        label = "Lokasi (Venue)",
                        isError = venueError,
                        errorMessage = "Lokasi wajib diisi"
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    // 4. Lineup
                    DarkTextField(
                        value = uiState.artistLineup,
                        onValueChange = {
                            viewModel.updateUiState(uiState.copy(artistLineup = it))
                            lineupError = false
                        },
                        label = "Artis Lineup",
                        isError = lineupError,
                        errorMessage = "Lineup wajib diisi"
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    // 5. Harga
                    Row(modifier = Modifier.fillMaxWidth()) {
                        Box(modifier = Modifier.weight(1f)) {
                            DarkTextField(
                                value = uiState.priceReguler,
                                onValueChange = {
                                    viewModel.updateUiState(uiState.copy(priceReguler = it))
                                    priceRegError = false
                                },
                                label = "Harga Reguler",
                                isNumber = true,
                                isError = priceRegError,
                                errorMessage = "Wajib"
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Box(modifier = Modifier.weight(1f)) {
                            DarkTextField(
                                value = uiState.priceVip,
                                onValueChange = {
                                    viewModel.updateUiState(uiState.copy(priceVip = it))
                                    priceVipError = false
                                },
                                label = "Harga VIP",
                                isNumber = true,
                                isError = priceVipError,
                                errorMessage = "Wajib"
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))

                    // 6. Poster (Opsional)
                    DarkTextField(
                        value = uiState.posterImage,
                        onValueChange = { viewModel.updateUiState(uiState.copy(posterImage = it)) },
                        label = "Link Gambar Poster (Opsional)"
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // --- TOMBOL SIMPAN ---
            Button(
                onClick = {
                    // 👇 CEK VALIDASI KOLOM KOSONG DULU
                    var isValid = true
                    if (uiState.eventName.isBlank()) { nameError = true; isValid = false }
                    if (uiState.date.isBlank()) { dateError = true; isValid = false }
                    if (uiState.venue.isBlank()) { venueError = true; isValid = false }
                    if (uiState.artistLineup.isBlank()) { lineupError = true; isValid = false }
                    if (uiState.priceReguler.isBlank()) { priceRegError = true; isValid = false }
                    if (uiState.priceVip.isBlank()) { priceVipError = true; isValid = false }

                    // Jika semua kolom OK, baru munculkan Dialog Konfirmasi
                    if (isValid) {
                        showConfirmationDialog = true
                    }
                },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AccentBlue),
                enabled = !uiState.isLoading
            ) {
                Text(
                    text = if (uiState.isLoading) "MENYIMPAN..." else "SIMPAN DATA",
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedButton(
                onClick = onCancel,
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color.Gray),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = TextWhite)
            ) {
                Text("BATAL")
            }
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

// ... (Sertakan helper DarkDatePickerField & DarkTextField yang ada di jawaban sebelumnya) ...

// --- Helper Date Picker ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DarkDatePickerField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    isError: Boolean = false,
    errorMessage: String? = null
) {
    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()
    val AccentBlue = Color(0xFF1D4ED8)
    val TextWhite = Color.White
    val ErrorRed = Color(0xFFEF4444)

    OutlinedTextField(
        value = value,
        onValueChange = { },
        label = { Text(label, color = if (isError) ErrorRed else Color.Gray) },
        readOnly = true,
        trailingIcon = {
            if (isError) Icon(Icons.Default.Warning, contentDescription = null, tint = ErrorRed)
            else IconButton(onClick = { showDatePicker = true }) {
                Icon(Icons.Default.DateRange, contentDescription = null, tint = AccentBlue)
            }
        },
        isError = isError,
        supportingText = {
            if (isError && errorMessage != null) Text(errorMessage, color = ErrorRed, style = MaterialTheme.typography.bodySmall)
        },
        modifier = Modifier.fillMaxWidth().clickable { showDatePicker = true },
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = if (isError) ErrorRed else AccentBlue,
            unfocusedBorderColor = if (isError) ErrorRed else Color.Gray,
            errorBorderColor = ErrorRed,
            focusedTextColor = TextWhite,
            unfocusedTextColor = TextWhite
        ),
        interactionSource = remember { MutableInteractionSource() }.also { src ->
            LaunchedEffect(src) { src.interactions.collect { if (it is PressInteraction.Release) showDatePicker = true } }
        }
    )

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let {
                        onValueChange(SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date(it)))
                    }
                    showDatePicker = false
                }) { Text("OK", fontWeight = FontWeight.Bold, color = AccentBlue) }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("Batal", color = Color.Gray) }
            }
        ) { DatePicker(state = datePickerState) }
    }
}

// --- Helper Text Field ---
@Composable
fun DarkTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    isNumber: Boolean = false,
    isError: Boolean = false,
    errorMessage: String? = null
) {
    val AccentBlue = Color(0xFF1D4ED8)
    val TextWhite = Color.White
    val ErrorRed = Color(0xFFEF4444)

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, color = if (isError) ErrorRed else Color.Gray) },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        isError = isError,
        trailingIcon = { if (isError) Icon(Icons.Default.Warning, null, tint = ErrorRed) },
        supportingText = {
            if (isError && errorMessage != null) Text(errorMessage, color = ErrorRed, style = MaterialTheme.typography.bodySmall)
        },
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = if (isError) ErrorRed else AccentBlue,
            unfocusedBorderColor = if (isError) ErrorRed else Color.Gray,
            errorBorderColor = ErrorRed,
            focusedTextColor = TextWhite,
            unfocusedTextColor = TextWhite,
            cursorColor = AccentBlue
        ),
        keyboardOptions = if (isNumber) KeyboardOptions(keyboardType = KeyboardType.Number) else KeyboardOptions.Default,
        singleLine = true
    )
}