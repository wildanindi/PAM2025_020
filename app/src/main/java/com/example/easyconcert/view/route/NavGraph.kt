package com.example.easyconcert.view.route


import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.easyconcert.data.container.AppContainer
import com.example.easyconcert.view.uicontroller.*
import com.example.easyconcert.viewmodel.HomeViewModel
import com.example.easyconcert.viewmodel.provider.PenyediaViewModel

// 1. DEFINISI RUTE (Alamat Halaman)
// Menggunakan sealed class agar nama rute konsisten dan tidak typo
sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Register : Screen("register")
    object Home : Screen("home")
    object MyTicket : Screen("my_ticket")
    object Profile : Screen("profile")

    object History : Screen("history")

    // Rute untuk Crew
    object Validation : Screen("validation")

    // Rute dengan Argumen (Mengirim Data)
    object Detail : Screen("detail/{concertId}") {
        fun createRoute(concertId: Int) = "detail/$concertId"
    }

    object Checkout : Screen("checkout/{concertId}/{category}/{quantity}") {
        fun createRoute(concertId: Int, category: String, quantity: Int) =
            "checkout/$concertId/$category/$quantity"
    }
}

// 2. NAVIGASI UTAMA
@Composable
fun ConcertNavGraph(
    navController: NavHostController,
    appContainer: AppContainer, // Container disuntikkan ke sini untuk diteruskan ke Screen
    modifier: Modifier = Modifier
) {
    val homeViewModel: HomeViewModel = viewModel(factory = PenyediaViewModel.Factory)


    NavHost(
        navController = navController,
        startDestination = Screen.Login.route, // Halaman pertama kali muncul
        modifier = modifier
    ) {

        // --- HALAMAN AUTH ---

        composable(Screen.Login.route) {
            HalamanLogin(
                onLoginSuccess = { routeTujuan ->
                    navController.navigate(routeTujuan) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onRegisterClick = {
                    navController.navigate(Screen.Register.route)
                },
                appContainer = appContainer
            )
        }

        composable(Screen.Register.route) {
            HalamanRegister(
                onRegisterSuccess = {
                    navController.popBackStack() // Kembali ke login
                },
                onLoginClick = {
                    navController.popBackStack()
                },
                appContainer = appContainer
            )
        }

        // --- HALAMAN FANS (UTAMA) ---

        composable(Screen.Home.route) {
            HalamanHome(
                onConcertClick = { concertId ->
                    // Pindah ke Detail membawa ID Konser
                    navController.navigate(Screen.Detail.createRoute(concertId))
                },
                onMyTicketClick = { navController.navigate(Screen.MyTicket.route) },
                onProfileClick = { navController.navigate(Screen.Profile.route) },
                appContainer = appContainer
            )
        }

        composable(
            route = Screen.Detail.route,
            arguments = listOf(navArgument("concertId") { type = NavType.IntType })
        ) { backStackEntry ->
            // Ambil ID dari rute
            val concertId = backStackEntry.arguments?.getInt("concertId") ?: 0

            HalamanDetail(
                concertId = concertId,
                onBuyClick = { cId, category, qty ->
                    // Pindah ke Checkout membawa data belanja
                    navController.navigate(Screen.Checkout.createRoute(cId, category, qty))
                },
                onBackClick = { navController.popBackStack() },
                appContainer = appContainer
            )
        }

        composable(
            route = Screen.Checkout.route,
            arguments = listOf(
                navArgument("concertId") { type = NavType.IntType },
                navArgument("category") { type = NavType.StringType },
                navArgument("quantity") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val concertId = backStackEntry.arguments?.getInt("concertId") ?: 0
            val category = backStackEntry.arguments?.getString("category") ?: ""
            val quantity = backStackEntry.arguments?.getInt("quantity") ?: 1

            HalamanCheckout(
                concertId = concertId,
                category = category,
                quantity = quantity,
                onPaymentSuccess = {
                    // Jika sukses bayar, langsung ke My Ticket & hapus history checkout
                    navController.navigate(Screen.MyTicket.route) {
                        popUpTo(Screen.Home.route)
                    }
                },
                onBackClick = { navController.popBackStack() },
                appContainer = appContainer
            )
        }

        composable(Screen.MyTicket.route) {
            HalamanMyTicket(
                onHomeClick = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                },
                onProfileClick = { navController.navigate(Screen.Profile.route) },
                appContainer = appContainer
            )
        }

        composable(Screen.History.route) {
            HalamanHistory(
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(Screen.Profile.route) {
            HalamanProfile(
                onLogout = {
                    // Logout: Hapus semua history, kembali ke Login
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                onHomeClick = { navController.navigate(Screen.Home.route) },
                onMyTicketClick = { navController.navigate(Screen.MyTicket.route) },
                onHistoryClick = { navController.navigate(Screen.History.route) },
                appContainer = appContainer

            )
        }

        // --- HALAMAN CREW ---

        composable(Screen.Validation.route) {
            HalamanValidasi(
                viewModel = homeViewModel,
                onAddConcertClick = {
                    navController.navigate("form_concert/0")
                },
                onEditConcertClick = { id ->
                    navController.navigate("form_concert/$id")
                },
                onLogoutClick = {
                    // Kembali ke Login
                    navController.navigate(Screen.Login.route) {
                        // Hapus semua tumpukan halaman agar tombol Back tidak kembali ke Validasi
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable(
            route = "form_concert/{id}",
            arguments = listOf(navArgument("id") { type = NavType.IntType })
        ) { backStackEntry ->
            val concertId = backStackEntry.arguments?.getInt("id") ?: 0

            HalamanFormKonser(
                concertId = if (concertId == 0) null else concertId,
                onSuccess = {
                    // 👇 3. INI KUNCINYA!
                    // Sebelum kembali, paksa HomeViewModel ambil data terbaru
                    homeViewModel.getConcerts()

                    navController.popBackStack()
                },
                onCancel = {
                    navController.popBackStack()
                }
            )
        }
    }
}