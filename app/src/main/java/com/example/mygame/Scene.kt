package com.example.mygame

import android.content.Context
import android.view.MotionEvent
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Canvas



interface Scene {

    var game : GameEngine
    //var renderState: MutableState<Int>
    // Вызывается для обновления логики (движение, физика, AI)
    fun update()

    // Вызывается для отрисовки кадра
    @Composable
    fun render()

    // Обработка касаний

    fun onTouchEvent(event: MotionEvent)

    // Вызывается при переходе на эту сцену (инициализация)
    fun onEnter()

    // Вызывается при уходе со сцены (очистка ресурсов)
    fun onExit()
}