package com.example.mygame

import android.content.Context
import android.util.Log
import android.view.MotionEvent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource

class SettingScene(override var game: GameEngine,context: Context) : Scene {
    override fun update() {

    }

    val X: Float = context.resources.configuration.screenHeightDp.toFloat()
    val Y: Float = context.resources.configuration.screenWidthDp.toFloat()

    val displayMetrics = context.resources.displayMetrics
    val screenXpx = displayMetrics.widthPixels.toFloat()
    val screenYpx = displayMetrics.heightPixels.toFloat()

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

        button_return.Render()
    }

    override fun onTouchEvent(event: MotionEvent) {
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {

            }
            MotionEvent.ACTION_UP -> {
                val mx = event.getX(0)
                val my = event.getY(0)

                //Log.d("","$screenXpx")
                //Log.d("","$screenYpx")


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
        TODO("Not yet implemented")
    }

    override fun onExit() {
        TODO("Not yet implemented")
    }

}