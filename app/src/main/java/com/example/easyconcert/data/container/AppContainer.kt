package com.example.easyconcert.data.container


import com.example.easyconcert.data.repository.AuthRepository
import com.example.easyconcert.data.repository.ConcertRepository
import com.example.easyconcert.data.repository.TicketRepository

interface AppContainer {
    val authRepository: AuthRepository
    val concertRepository: ConcertRepository
    val ticketRepository: TicketRepository

    
}