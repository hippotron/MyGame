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


class MenuScene(override var game: GameEngine,context: Context): Scene{

    //var mas=arrayOf(Color.Black,Color.Blue,Color.Red,Color.Yellow,Color.Green,Color.Gray,Color.Cyan)

    val displayMetrics = context.resources.displayMetrics
    val screenXpx = displayMetrics.widthPixels
    val screenYpx = displayMetrics.heightPixels

    val button_game = ButtonImage(screenXpx*0.2f,screenYpx*0.38f,
        screenXpx*0.6f,screenYpx*0.12f,R.drawable.game_two)

    val button_setting = ButtonImage((screenXpx*0.2f),(screenYpx*0.5f),
        screenXpx*0.6f,(screenYpx*0.12f),R.drawable.setting_two)

    val button_authors = ButtonImage((screenXpx*0.2f),(screenYpx*0.623f),
        screenXpx*0.6f,screenYpx*0.12f,R.drawable.authors_two)


    override fun update() {
        TODO("Not yet implemented")
    }

    @Composable
    override fun render() {
        //background
        Image(
            painter = painterResource(id = R.drawable.background_menu),// Укажите ваш файл
            contentDescription = "фон", // Описание для доступности (обязательно!)
            contentScale = ContentScale.FillBounds,
            modifier = Modifier.fillMaxSize()
        )
        button_game.Render()
        button_setting.Render()
        button_authors.Render()
    }

    override fun onTouchEvent(event: MotionEvent) {
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {

                //Log.d("","$screenXpx")
                //Log.d("","$screenYpx")


                if (event.pointerCount == 1) {
                    val mx = event.getX(0)
                    val my = event.getY(0)

                    /*Log.d("","$screenXpx")
                    Log.d("","$screenYpx")
                    Log.d("","$mx")
                    Log.d("","$my")
                     */

                    if (button_game.click(mx,my)==true){
                        //Log.d("","444")
                        game.CurrentScene="Game"
                    }

                    if (button_setting.click(mx,my)==true){
                        game.CurrentScene="Setting"
                    }

                    if (button_authors.click(mx,my)==true){
                        game.CurrentScene="Authors"
                    }


                    game.forceUpdate++   // увеличиваем счётчик для принудительной перерисовки
                }
                true
            }
            MotionEvent.ACTION_POINTER_DOWN -> {
                if (event.pointerCount == 2) {
                    //Log.d("Touch", "Два пальца — второе касание")
                    Log.d("","111")
                } else if (event.pointerCount > 2) {
                    //Log.d("Touch", "Больше двух пальцев")
                }
                true
            }
        }
    }

    override fun onEnter() {

    }

    override fun onExit() {

    }


}
