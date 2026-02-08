package com.example.mygame

import android.content.Context
import android.view.MotionEvent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource

class RulesScene (override var game: GameEngine, val context: Context): Scene {

    val displayMetrics = context.resources.displayMetrics
    val screenX = displayMetrics.widthPixels
    val screenY = displayMetrics.heightPixels

    val button_return = ButtonImage((screenX*0.05).toInt(),(screenY*0.035).toInt(),
        (screenX*0.3).toInt(),(screenY*0.1).toInt(),R.drawable.image_return)

    val mini_fon = ButtonImage((screenX*0.1).toInt(),(screenY*0.15).toInt(),
        (screenX*0.8).toInt(),(screenY*0.8).toInt(),R.drawable.mini_fon)

    val button_arrow_right = ButtonImage((screenX*0.53).toInt(),(screenY*0.87).toInt(),
        (screenX*0.07).toInt(),(screenY*0.05).toInt(),R.drawable.arrow_right)

    val button_arrow_left = ButtonImage((screenX*0.43).toInt(),(screenY*0.87).toInt(),
        (screenX*0.07).toInt(),(screenY*0.05).toInt(),R.drawable.arrow_left)


    override fun update() {

    }

    override fun onTouchEvent(event: MotionEvent) {
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                val mx = event.x.toInt()
                val my = event.y.toInt()

                if (button_return.click(mx,my)){
                    game.goBackScene()
                }

                game.forceUpdate++
            }
        }
    }

    @Composable
    override fun render() {
        //background
        Image(
            painter = painterResource(id = R.drawable.background_menu),
            contentDescription = "фон",
            contentScale = ContentScale.FillBounds,
            modifier = Modifier.fillMaxSize()
        )

        mini_fon.Render()
        button_return.Render()



        button_arrow_left.Render()
        button_arrow_right.Render()
    }

    override fun onEnter() {

    }

    override fun onExit() {

    }

}