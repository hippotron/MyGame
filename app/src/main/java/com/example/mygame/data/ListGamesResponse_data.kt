package com.example.mygame.data

import java.time.LocalDateTime

data class ListGamesResponse_data(
    val games: List<Game>
)

data class Game(
    val gameId: Int,
    val date: LocalDateTime
)
