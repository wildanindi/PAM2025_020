package com.example.easyconcert.viewmodel.provider


import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.easyconcert.ConcertApp
import com.example.easyconcert.viewmodel.AuthViewModel
import com.example.easyconcert.viewmodel.BookingViewModel
import com.example.easyconcert.viewmodel.HistoryViewModel
import com.example.easyconcert.viewmodel.HomeViewModel
import com.example.easyconcert.viewmodel.ManageConcertViewModel
import com.example.easyconcert.viewmodel.ValidationViewModel

object PenyediaViewModel {
    val Factory = viewModelFactory {

        // 1. AuthViewModel
        initializer {
            AuthViewModel(concertApp().container.authRepository)
        }

        // 2. HomeViewModel
        initializer {
            HomeViewModel(concertApp().container.concertRepository)
        }

        // 3. BookingViewModel
        initializer {
            BookingViewModel(concertApp().container.ticketRepository)
        }

        // 4. ValidationViewModel
        initializer {
            ValidationViewModel(concertApp().container.ticketRepository)
        }

        initializer {
            ManageConcertViewModel(concertApp().container.concertRepository)
        }

        // Di dalam initializer block:
        initializer {
            HistoryViewModel(concertApp().container.concertRepository)
        }
    }
}

// Fungsi ekstensi agar kode lebih rapi
fun CreationExtras.concertApp(): ConcertApp =
    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as ConcertApp)