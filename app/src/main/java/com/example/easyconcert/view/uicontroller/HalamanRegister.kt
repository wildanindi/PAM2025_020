package com.example.easyconcert.view.uicontroller

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.easyconcert.data.container.AppContainer
import com.example.easyconcert.viewmodel.AuthViewModel
import com.example.easyconcert.viewmodel.provider.PenyediaViewModel

// Definisi Warna sesuai Gambar
val DarkBackground = Color(0xFF020617) // Warna Gelap Navy/Hitam
val InputBackground = Color(0xFFD9D9D9) // Warna Abu-abu kolom input
val ButtonBlue = Color(0xFF1D4ED8) // Warna Biru Tombol
val TextWhite = Color.White

@Composable
fun HalamanRegister(
    onRegisterSuccess: () -> Unit,
    onLoginClick: () -> Unit, // Ini kita pakai untuk tombol Back juga
    appContainer: AppContainer
) {
    val viewModel: AuthViewModel = viewModel(factory = PenyediaViewModel.Factory)
    val loginState = viewModel.loginState.collectAsState().value

    // State Input Form
    var fullName by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val roles = listOf("FAN", "CREW")
    var selectedRole by remember { mutableStateOf(roles[0]) }

    // Logic Navigasi Sukses
    LaunchedEffect(loginState) {
        if (loginState.token == "REGISTER_SUCCESS") {
            onRegisterSuccess()
        }
    }

    // Container Utama (Background Gelap)
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {
        // Tombol Back di Pojok Kiri Atas
        IconButton(
            onClick = onLoginClick,
            modifier = Modifier
                .padding(top = 48.dp, start = 24.dp)
                .align(Alignment.TopStart)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.1f)) // Efek transparan dikit
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = TextWhite
            )
        }

        // Konten Form (Scrollable biar aman di layar kecil)
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp)
                .verticalScroll(rememberScrollState()), // Agar bisa discroll
            horizontalAlignment = Alignment.Start, // Rata kiri sesuai gambar role
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.height(80.dp)) // Jarak dari atas

            // --- INPUT FIELDS (Custom Style) ---
            CustomInputData(
                value = fullName,
                onValueChange = { fullName = it },
                placeholder = "Nama Lengkap"
            )

            Spacer(modifier = Modifier.height(16.dp))

            CustomInputData(
                value = username,
                onValueChange = { username = it },
                placeholder = "Username"
            )

            Spacer(modifier = Modifier.height(16.dp))

            CustomInputData(
                value = password,
                onValueChange = { password = it },
                placeholder = "Password",
                isPassword = true
            )

            Spacer(modifier = Modifier.height(32.dp))

            // --- ROLE SECTION ---
            Text(
                text = "ROLE",
                color = TextWhite,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // Radio Buttons Vertikal
            Column(modifier = Modifier.fillMaxWidth()) {
                roles.forEach { role ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .selectable(
                                selected = (role == selectedRole),
                                onClick = { selectedRole = role }
                            )
                    ) {
                        RadioButton(
                            selected = (role == selectedRole),
                            onClick = { selectedRole = role },
                            colors = RadioButtonDefaults.colors(
                                selectedColor = ButtonBlue,
                                unselectedColor = Color.Gray
                            )
                        )
                        Text(
                            text = role.lowercase().replaceFirstChar { it.uppercase() }, // Jadi "Fan" bukan "FAN"
                            color = TextWhite,
                            fontSize = 16.sp,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Error Message
            if (loginState.error != null) {
                Text(
                    text = loginState.error,
                    color = Color(0xFFFF5252), // Merah terang
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            Spacer(modifier = Modifier.height(24.dp))

            // --- TOMBOL REGISTER ---
            Button(
                onClick = {
                    viewModel.register(fullName, username, password, selectedRole)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp), // Sudut membulat
                colors = ButtonDefaults.buttonColors(
                    containerColor = ButtonBlue,
                    disabledContainerColor = ButtonBlue.copy(alpha = 0.5f)
                ),
                enabled = !loginState.isLoading
            ) {
                if (loginState.isLoading) {
                    CircularProgressIndicator(color = TextWhite, modifier = Modifier.size(24.dp))
                } else {
                    Text(
                        text = "Register",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextWhite
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

// --- KOMPONEN INPUT CUSTOM (Biar Rapi) ---
@Composable
fun CustomInputData(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    isPassword: Boolean = false
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = { Text(placeholder, color = Color.Gray) },
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp), // Tinggi standar tombol
        shape = RoundedCornerShape(16.dp), // Sangat membulat sesuai gambar
        colors = TextFieldDefaults.colors(
            focusedContainerColor = InputBackground,
            unfocusedContainerColor = InputBackground,
            disabledContainerColor = InputBackground,
            focusedIndicatorColor = Color.Transparent, // Hilangkan garis bawah
            unfocusedIndicatorColor = Color.Transparent,
            cursorColor = Color.Black
        ),
        visualTransformation = if (isPassword) PasswordVisualTransformation() else androidx.compose.ui.text.input.VisualTransformation.None,
        singleLine = true
    )
}