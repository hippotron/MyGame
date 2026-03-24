package com.example.mygame.data

import com.example.mygame.Economic
import com.example.mygame.Pole.Data_cell

data class GameMap_data(
    var economic: Map<String, Economic>,  // Используем String как ключ
    var listPlayer: List<player_data>,
    var hod_player: Int,
    var pole: Pole_data
)

data class Pole_data(
    var pole: List<List<Data_cell>>
)

