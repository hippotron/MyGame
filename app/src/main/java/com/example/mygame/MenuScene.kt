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
    val screenX = displayMetrics.widthPixels
    val screenY = displayMetrics.heightPixels

    val button_new_game = ButtonImage((screenX*0.2).toInt(),(screenY*0.41).toInt(),
        (screenX*0.6).toInt(),(screenY*0.1).toInt(),R.drawable.new_game)

    val button_setting = ButtonImage((screenX*0.2).toInt(),(screenY*0.515).toInt(),
        (screenX*0.6).toInt(),(screenY*0.1).toInt(),R.drawable.settings)

    val button_is_look_rupes = ButtonImage((screenX*0.2).toInt(),(screenY*0.62).toInt(),
        (screenX*0.6).toInt(),(screenY*0.1).toInt(),R.drawable.is_look_rules)

    val button_authors = ButtonImage((screenX*0.2).toInt(),(screenY*0.725).toInt(),
        (screenX*0.6).toInt(),(screenY*0.1).toInt(),R.drawable.authors)


    override fun update() {

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
        button_new_game.Render()
        button_setting.Render()
        button_is_look_rupes.Render()
        button_authors.Render()
    }

    override fun onTouchEvent(event: MotionEvent) {
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {

                //Log.d("","$screenXpx")
                //Log.d("","$screenYpx")


                if (event.pointerCount == 1) {
                    val mx = event.x.toInt()
                    val my = event.y.toInt()

                    /*Log.d("","$screenXpx")
                    Log.d("","$screenYpx")
                    Log.d("","$mx")
                    Log.d("","$my")
                     */

                    if (button_new_game.click(mx,my)==true){
                        //Log.d("","444")
                        game.updateScene("Game")

                    }

                    if (button_setting.click(mx,my)==true){
                        game.updateScene("Setting")
                    }

                    if (button_is_look_rupes.click(mx,my)){
                        game.updateScene("Rules")
                    }

                    if (button_authors.click(mx,my)==true){
                        game.updateScene("Authors")
                    }


                    game.forceUpdate++   // увеличиваем счётчик для принудительной перерисовки
                }
            }
            MotionEvent.ACTION_POINTER_DOWN -> {
                if (event.pointerCount == 2) {
                    //Log.d("Touch", "Два пальца — второе касание")
                    //Log.d("","111")
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
