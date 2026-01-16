package com.example.easyconcert.view.uicontroller

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.easyconcert.data.container.AppContainer
import com.example.easyconcert.viewmodel.AuthViewModel
import com.example.easyconcert.viewmodel.provider.PenyediaViewModel

@Composable
fun HalamanLogin(
    onLoginSuccess: (String) -> Unit, // Callback mengirim Role (FAN/CREW)
    onRegisterClick: () -> Unit,
    appContainer: AppContainer
) {
    // Inisialisasi ViewModel
    val viewModel: AuthViewModel = viewModel(factory = PenyediaViewModel.Factory)
    val loginState = viewModel.loginState.collectAsState().value

    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    // Warna Tema (Sama dengan Register)
    val DarkBackground = Color(0xFF020617)
    val InputBackground = Color(0xFFD9D9D9)
    val ButtonBlue = Color(0xFF1D4ED8)
    val TextWhite = Color.White

    // Container Utama
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp)
                .verticalScroll(rememberScrollState()), // Agar aman di layar kecil
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Judul
            Text(
                text = "EasyConcert",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = TextWhite
            )
            Text(
                text = "Silakan Login untuk melanjutkan",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(48.dp))

            // --- INPUT USERNAME ---
            LoginInputData(
                value = username,
                onValueChange = { username = it },
                placeholder = "Username",
                backgroundColor = InputBackground
            )

            Spacer(modifier = Modifier.height(16.dp))

            // --- INPUT PASSWORD ---
            LoginInputData(
                value = password,
                onValueChange = { password = it },
                placeholder = "Password",
                isPassword = true,
                backgroundColor = InputBackground
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Tampilkan Error jika Login Gagal
            if (loginState.error != null) {
                Text(
                    text = loginState.error,
                    color = Color(0xFFFF5252),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            // --- TOMBOL LOGIN ---
            Button(
                onClick = {
                    viewModel.login(username, password)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp),
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
                        text = "LOGIN",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextWhite
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // --- TOMBOL REGISTER ---
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Belum punya akun?", color = Color.Gray)
                TextButton(onClick = onRegisterClick) {
                    Text(
                        "Daftar di sini",
                        color = ButtonBlue,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }

    // --- LOGIC NAVIGASI (Biarkan Seperti Semula) ---
    LaunchedEffect(loginState) {
        if (loginState.token != null) {
            val role = loginState.role
            println("CEK_ROLE: Server bilang role-nya adalah $role")

            if (role.equals("CREW", ignoreCase = true)) {
                onLoginSuccess("validation") // Arahkan ke Validasi
            } else {
                onLoginSuccess("home") // Arahkan ke Home
            }
        }
    }
}

// --- KOMPONEN INPUT KHUSUS LOGIN ---
@Composable
fun LoginInputData(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    isPassword: Boolean = false,
    backgroundColor: Color
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = { Text(placeholder, color = Color.Gray) },
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        shape = RoundedCornerShape(16.dp),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = backgroundColor,
            unfocusedContainerColor = backgroundColor,
            disabledContainerColor = backgroundColor,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            cursorColor = Color.Black
        ),
        visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None,
        keyboardOptions = if (isPassword) KeyboardOptions(keyboardType = KeyboardType.Password) else KeyboardOptions.Default,
        singleLine = true
    )
}