package com.example.mygame.Pole

import com.example.mygame.PackagePlayer.Player
import com.example.mygame.Terrain

class Cell (
    var player: Player?,
    var protection: Int,
    var land: Terrain
)

data class Data_cell(
    var player: Int?,
    var protection: Int,
    val land: Terrain
)