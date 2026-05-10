package com.example.mygame.Scene

import android.content.Context
import android.view.MotionEvent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.example.mygame.ButtonImage
import com.example.mygame.Scene.ForScene.GameEngine
import com.example.mygame.R
import com.example.mygame.Scene.ForScene.Scene
import com.example.mygame.SoundPlayer

class SettingScene(override var game: GameEngine, context: Context) : Scene {

    val displayMetrics = context.resources.displayMetrics
    val screenXpx = displayMetrics.widthPixels.toFloat()
    val screenYpx = displayMetrics.heightPixels.toFloat()

    val button_return = ButtonImage(
        (screenXpx * 0.1).toInt(), (screenYpx * 0.07).toInt(),
        (screenXpx * 0.3).toInt(), (screenYpx * 0.1).toInt(), R.drawable.image_return
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

    override fun update() {

    }

    override fun onEnter() {

    }

    override fun onExit() {

    }

}