package com.example.mygame.data

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.example.mygame.PackagePlayer.OtherForPlayer.Build
import com.example.mygame.PackagePlayer.OtherForPlayer.Unit
import com.example.mygame.PackagePlayer.OtherForPlayer.Unit_data

data class player_data(
    var name: String,
    var color: Int,
    var units: List<Unit_data>,
    var buildings: List<Build>,
    var kazna: Int,
    var income: Int,
    var sale: Int,
)
