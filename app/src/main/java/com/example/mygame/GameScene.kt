package com.example.mygame

import android.content.Context
import android.view.MotionEvent
import androidx.compose.runtime.Composable


class GameScene(override var game: GameEngine, context: Context) : Scene {
    val contexts = context
    val displayMetrics = context.resources.displayMetrics
    val screenXpx = displayMetrics.widthPixels.toFloat()
    val screenYpx = displayMetrics.heightPixels.toFloat()

    var pole = Pole( context = contexts)

    val button_point = ButtonImage((screenXpx*0.9f),(screenYpx*0.1f),
        (screenXpx*0.05f),(screenYpx*0.1f),R.drawable.point)

    val player = Player("1", 100,40, pole)
    //val player2 = Player("2", 100,40, pole)

    var hod_player=0

    val listPlayers = arrayListOf<Player>(player)

    override fun update() {

    }

    @Composable
    override fun render() {
        //val context = LocalContext.current
        pole.Render()
        button_point.Render()

        listPlayers[hod_player].Render()
    }

    override fun onTouchEvent(event: MotionEvent) {
        pole.onTouch(event)
        listPlayers[hod_player].onTouch(event)

        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
            }

            MotionEvent.ACTION_UP -> {
                val mx = event.getX(0)
                val my = event.getY(0)

                if (event.pointerCount == 1) {
                    if (button_point.click(mx,my)==true){
                        pole = Pole(context = contexts)
                        game.CurrentScene="Menu"
                    }
                }

                // Принудительное обновление сцены
                game.forceUpdate++

                // Также можно добавить обновление массива игроков если нужно
                // updatePlayersList()
            }
        }
    }


    override fun onEnter() {

    }

    override fun onExit() {

    }
}