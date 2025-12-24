package com.example.mygame

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

class GameEngine(){
    // var CurrentScene : String =""
    var CurrentScene by mutableStateOf("Menu")
    var forceUpdate by mutableStateOf(0)

}