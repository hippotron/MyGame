package com.example.mygame

import android.util.MutableInt
import androidx.compose.runtime.MutableState

class Cell (
    var player: Player?,
    var protection: Int,
    val land: Terrain
){

}