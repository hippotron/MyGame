package com.example.mygame.data

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import java.util.UUID

data class profile_data(
    var playerName: String,
    var playerId: UUID
){
    companion object {
        var DEFAULT_PROFILE by mutableStateOf(profile_data("Player1", UUID.randomUUID()))
    }
}
