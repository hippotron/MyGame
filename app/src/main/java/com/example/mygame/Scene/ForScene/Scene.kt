package com.example.mygame.Scene.ForScene

import android.view.MotionEvent
import androidx.compose.runtime.Composable

interface Scene {

    var game : GameEngine
    //var renderState: MutableState<Int>
    // Вызывается для обновления логики
    fun update()

    // Вызывается для отрисовки крафики
    @Composable
    fun render()

    // Обработка касаний
    fun onTouchEvent(event: MotionEvent)

    // Вызывается при переходе на эту сцену (инициализация)
    fun onEnter()

    // Вызывается при уходе со сцены (очистка ресурсов)
    fun onExit()
}