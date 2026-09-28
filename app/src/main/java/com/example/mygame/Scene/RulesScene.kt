package com.example.mygame.Scene

import android.content.Context
import android.view.MotionEvent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.mygame.ButtonImage
import com.example.mygame.Economic
import com.example.mygame.PictureRender
import com.example.mygame.R
import com.example.mygame.Scene.ForScene.GameEngine
import com.example.mygame.Scene.ForScene.Scene
import com.example.mygame.SoundPlayer
import com.example.mygame.Terrain

class RulesScene(
    override var game: GameEngine,
    val context: Context,
    val economic: HashMap<Terrain, Economic>
) : Scene {

    val displayMetrics = context.resources.displayMetrics
    val screenX = displayMetrics.widthPixels
    val screenY = displayMetrics.heightPixels

    val button_return = ButtonImage(
        (screenX * 0.18).toInt(), (screenY * 0.012).toInt(),
        (screenX * 0.64).toInt(), (screenY * 0.072).toInt(), R.drawable.rules_btn_back
    )

    val button_arrow_right = ButtonImage(
        (screenX * 0.735).toInt(), (screenY * 0.888).toInt(),
        (screenX * 0.12).toInt(), (screenY * 0.058).toInt(), R.drawable.rules_arrow_right
    )

    val button_arrow_left = ButtonImage(
        (screenX * 0.145).toInt(), (screenY * 0.888).toInt(),
        (screenX * 0.12).toInt(), (screenY * 0.058).toInt(), R.drawable.rules_arrow_left
    )

    var slide by mutableStateOf(1)

    private val soundPlayer = SoundPlayer(context)

    private val PicRender = PictureRender()

    override fun update() {}

    override suspend fun onTouchEvent(event: MotionEvent) {
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                val mx = event.x.toInt()
                val my = event.y.toInt()

                if (slide >= 2) {
                    if (button_arrow_left.click(mx, my)) {
                        soundPlayer.play(R.raw.button)
                        slide--
                    }
                }
                if (slide <= 3) {
                    if (button_arrow_right.click(mx, my)) {
                        soundPlayer.play(R.raw.button)
                        slide++
                    }
                }
                if (button_return.click(mx, my)) {
                    soundPlayer.play(R.raw.button)
                    game.goBackScene()
                }
                game.forceUpdate++
            }
        }
    }

    @Composable
    override fun render() {
        Box(modifier = Modifier.fillMaxSize()) {
            PicRender.RenderHeader(slide)

            if (slide >= 2) button_arrow_left.Render()
            if (slide <= 3) button_arrow_right.Render()
            button_return.Render()

            if (slide == 1) {
                PicRender.RenderStart()
            }
            if (slide == 2) {
                PicRender.RenderUnits()
            }
            if (slide == 3) {
                PicRender.RenderBuildings()
            }
            if (slide == 4) {
                PicRender.RenderBattle()
            }
        }
    }

    override fun onEnter() {
        slide = 1
    }

    override fun onExit() {}
}
