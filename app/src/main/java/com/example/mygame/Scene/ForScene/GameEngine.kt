package com.example.mygame.Scene.ForScene

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.mygame.R
import com.example.mygame.SoundPlayer
import io.ktor.client.plugins.cookies.HttpCookies

class GameEngine(){
    var CurrentScene by mutableStateOf("Menu")
    var PreviousScene by mutableStateOf("Menu") // Добавляем
    var forceUpdate by mutableStateOf(0)

    var backScene: Boolean = false

    // функция для перехода сцены
    fun updateScene(scene: String) {
        backScene=false
        PreviousScene = CurrentScene
        CurrentScene = scene

    }

    // функция для возращения сцены на предыдущую
    fun goBackScene() {
        backScene=true
        CurrentScene = PreviousScene

    }
}