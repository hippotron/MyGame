package com.example.mygame

import android.content.Context
import android.view.MotionEvent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource


class GameScene(override var game: GameEngine, context: Context) : Scene {
    val contexts = context
    val displayMetrics = context.resources.displayMetrics
    val screenXpx = displayMetrics.widthPixels
    val screenYpx = displayMetrics.heightPixels

    var pole = Pole(context = contexts)

    val button_point_back = ButtonImage((screenXpx*0.9).toInt(),(screenYpx*0.1).toInt(),
        (screenXpx*0.05).toInt(),(screenYpx*0.1).toInt(),R.drawable.point)

    var hod_player=0

    // Используем lateinit var вместо val, чтобы пересоздавать Player при смене Pole
    lateinit var player: Player
    lateinit var listPlayers: ArrayList<Player>

    init {
        createPlayer()
    }

    private fun createPlayer() {
        player = Player("1", 100,40, pole)
        listPlayers = arrayListOf(player)
    }

    override fun update() {

    }

    @Composable
    override fun render() {
        Image(
            painter = painterResource(id = R.drawable.background_menu),// Укажите ваш файл
            contentDescription = "фон", // Описание для доступности (обязательно!)
            contentScale = ContentScale.FillBounds,
            modifier = Modifier.fillMaxSize()
        )

        pole.Render()
        button_point_back.Render()

        listPlayers[hod_player].Render()
    }

    override fun onTouchEvent(event: MotionEvent) {
        pole.onTouch(event)
        listPlayers[hod_player].onTouch(event)

        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
            }

            MotionEvent.ACTION_UP -> {
                val mx = event.x.toInt()
                val my = event.y.toInt()

                if (event.pointerCount == 1) {
                    if (button_point_back.click(mx,my)==true){
                        // Пересоздаем Pole
                        pole = Pole(context = contexts)
                        // Пересоздаем Player с новым Pole
                        createPlayer()
                        game.CurrentScene="Menu"
                    }
                }

                // Принудительное обновление сцены
                game.forceUpdate++
            }
        }
    }

    override fun onEnter() {

    }

    override fun onExit() {

    }
}
