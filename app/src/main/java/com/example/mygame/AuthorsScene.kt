package com.example.mygame

import android.content.Context
import android.view.MotionEvent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MovableContent
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp

class AuthorsScene(override var game: GameEngine,context: Context) : Scene {
    override fun update() {

    }
    val displayMetrics = context.resources.displayMetrics
    val screenXpx = displayMetrics.widthPixels
    val screenYpx = displayMetrics.heightPixels

    val button_return = ButtonImage(screenXpx*0.1f,screenYpx*0.07f,
        screenXpx*0.3f,screenYpx*0.1f,R.drawable.image_return)

    @Composable
    override fun render() {
        //background
        Image(
            painter = painterResource(id = R.drawable.background_menu),// Укажите ваш файл
            contentDescription = "фон", // Описание для доступности (обязательно!)
            contentScale = ContentScale.FillBounds,
            modifier = Modifier.fillMaxSize()
        )

        Text(
            modifier = Modifier.offset(150.dp,150.dp),
            text = "Авторы"
        )

        button_return.Render()
    }

    override fun onTouchEvent(event: MotionEvent) {
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {

            }
            MotionEvent.ACTION_UP -> {
                val mx = event.getX(0)
                val my = event.getY(0)

                if (event.pointerCount == 1) {
                    if (button_return.click(mx,my)==true){
                        //Log.d("","ttt")
                        game.CurrentScene="Menu"
                    }
                }
                game.forceUpdate++
            }
        }
    }

    override fun onEnter() {

    }

    override fun onExit() {

    }
}