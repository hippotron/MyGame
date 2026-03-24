package com.example.mygame.PackagePlayer.OtherForPlayer


import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableIntStateOf
import com.example.mygame.PackageForKoor.koorOnInt
import com.example.mygame.Terrain

class Unit(
    var koorOnPole: koorOnInt,
    var type: Terrain,
    Size: Int = 60,
    //var canMove: Boolean,
    var Movement: Int = 2
) {
    var size: MutableState<Int> = mutableIntStateOf(Size)
}

data class Unit_data(
    var koorOnInt: koorOnInt,
    var type: Terrain,
    var Movement: Int,
    var size: Int
)