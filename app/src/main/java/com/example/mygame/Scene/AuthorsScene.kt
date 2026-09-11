package com.example.mygame.Scene

import android.content.Context
import android.view.MotionEvent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mygame.ButtonImage
import com.example.mygame.Scene.ForScene.GameEngine
import com.example.mygame.R
import com.example.mygame.Scene.ForScene.Scene
import com.example.mygame.SoundPlayer

class AuthorsScene(override var game: GameEngine, context: Context) : Scene {
    override fun update() {

    }
    val displayMetrics = context.resources.displayMetrics
    val screenXpx = displayMetrics.widthPixels
    val screenYpx = displayMetrics.heightPixels

    // Тот же размер, что у кнопок главного меню
    val button_return = ButtonImage(
        (screenXpx * 0.15).toInt(), (screenYpx * 0.04).toInt(),
        (screenXpx * 0.7).toInt(), (screenYpx * 0.095).toInt(), R.drawable.image_return
    )

    private val soundPlayer = SoundPlayer(context)

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
            modifier = Modifier.offset(x = 120.dp, y = 120.dp),
            text = "Авторы",
            color = Color.White,
            fontSize = 28.sp
        )

        button_return.Render()
    }

    override suspend fun onTouchEvent(event: MotionEvent) {
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {

            }
            MotionEvent.ACTION_UP -> {
                val mx = event.x.toInt()
                val my = event.y.toInt()

                if (event.pointerCount == 1) {
                    if (button_return.click(mx,my)==true){
                        //Log.d("","ttt")
                        //soundPlayer.play(R.raw.button)
                        game.goBackScene()
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