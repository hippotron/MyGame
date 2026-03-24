package com.example.mygame.Scene

import android.content.Context
import android.view.MotionEvent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import com.example.mygame.ButtonImage
import com.example.mygame.Economic
import com.example.mygame.Scene.ForScene.GameEngine
import com.example.mygame.GlobalParam.TextRender
import com.example.mygame.R
import com.example.mygame.Scene.ForScene.Scene
import com.example.mygame.Terrain

class RulesScene (override var game: GameEngine, val context: Context, val economic: HashMap<Terrain, Economic>):
    Scene {

    val displayMetrics = context.resources.displayMetrics
    val screenX = displayMetrics.widthPixels
    val screenY = displayMetrics.heightPixels

    val button_return = ButtonImage(
        (screenX * 0.05).toInt(), (screenY * 0.035).toInt(),
        (screenX * 0.3).toInt(), (screenY * 0.1).toInt(), R.drawable.image_return
    )

    val mini_fon = ButtonImage(
        (screenX * 0.09).toInt(), (screenY * 0.15).toInt(),
        (screenX * 0.83).toInt(), (screenY * 0.8).toInt(), R.drawable.mini_fon
    )

    val button_arrow_right = ButtonImage(
        (screenX * 0.53).toInt(), (screenY * 0.87).toInt(),
        (screenX * 0.07).toInt(), (screenY * 0.05).toInt(), R.drawable.arrow_right
    )

    val button_arrow_left = ButtonImage(
        (screenX * 0.43).toInt(), (screenY * 0.87).toInt(),
        (screenX * 0.07).toInt(), (screenY * 0.05).toInt(), R.drawable.arrow_left
    )

    val size_text=(screenY/110.1818).toInt()

    var slide by mutableStateOf(1)

    override fun update() {

    }

    override fun onTouchEvent(event: MotionEvent) {
        println("$screenX, $screenY, $size_text")
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                val mx = event.x.toInt()
                val my = event.y.toInt()

                if (slide==2 || slide==3) {
                    if (button_arrow_left.click(mx,my)) {
                        slide--
                        //println("--")
                    }

                }
                if (slide==1 || slide==2) {
                    if (button_arrow_right.click(mx,my)) {
                        slide++
                        //println("++")
                    }
                }

                if (button_return.click(mx,my)){
                    game.goBackScene()
                }

                game.forceUpdate++
            }
        }
    }

    @Composable
    override fun render() {
        val textMeasurer = rememberTextMeasurer()
        //background
        Image(
            painter = painterResource(id = R.drawable.background_menu),
            contentDescription = "фон",
            contentScale = ContentScale.FillBounds,
            modifier = Modifier.fillMaxSize()
        )


        mini_fon.Render()
        button_return.Render()

        Canvas(
            modifier = Modifier
                //.offset(x = (screenX*0.1).toInt().dp, y = (screenY*0.15).toInt().dp)
                .size(width = (screenX*0.34).toInt().dp, height = (screenY*0.8).toInt().dp)
        ) {
            if (slide==1) {
                render_rules_1(textMeasurer)
            }else if (slide==2){
                render_rules_2(textMeasurer)
            } else {
                render_rules_3(textMeasurer)
            }

            //render_rules_1(textMeasurer)
            //render_rules_2(textMeasurer)
            //render_rules_1(textMeasurer)
        }
        if (slide==2 || slide==3) {
            button_arrow_left.Render()
        }
        if (slide==1 || slide==2) {
            button_arrow_right.Render()
        }
    }

    fun DrawScope.render_rules_1(textMeasurer: TextMeasurer){
        TextRender(
            textMeasurer,
            name = "1. Игроки начинают: ",
            x = (screenX*0.15).toInt(),
            y = (screenY*0.17).toInt(),
            size = size_text,
            bold = true
        )
        TextRender(
            textMeasurer,
            name = "    · Со 80 монет",
            x = (screenX*0.15).toInt(),
            y = (screenY*0.21).toInt(),
            size = size_text,
            bold = true
        )
        TextRender(
            textMeasurer,
            name = "    · С 7 клеток ",
            x = (screenX*0.15).toInt(),
            y = (screenY*0.25).toInt(),
            size = size_text,
            bold = true
        )
        TextRender(
            textMeasurer,
            name = "    · С доходом 50 монет.",
            x = (screenX*0.15).toInt(),
            y = (screenY*0.29).toInt(),
            size = size_text,
            bold = true
        )

        TextRender(
            textMeasurer,
            name = "2. У игроков есть доход: ",
            x = (screenX*0.15).toInt(),
            y = (screenY*0.33).toInt(),
            size = size_text,
            bold = true
        )
        TextRender(
            textMeasurer,
            name = "    · Одна клетка приносит \n      ${economic[Terrain.LAND]!!.income} монет.",
            x = (screenX*0.15).toInt(),
            y = (screenY*0.37).toInt(),
            size = size_text,
            bold = true
        )

        TextRender(
            textMeasurer,
            name = "3. Юниты и здания деньги \n не приносят.",
            x = (screenX*0.15).toInt(),
            y = (screenY*0.44).toInt(),
            size = size_text,
            bold = true
        )
        TextRender(
            textMeasurer,
            name = "4. Игроки могут ставить юнитов/здания. ",
            x = (screenX*0.15).toInt(),
            y = (screenY*0.51).toInt(),
            size = size_text,
            bold = true
        )

        TextRender(
            textMeasurer,
            name = "4.1. Юниты: ",
            x = (screenX*0.15).toInt(),
            y = (screenY*0.58).toInt(),
            size = size_text,
            bold = true
        )

        TextRender(
            textMeasurer,
            name = "    · Unit1 стоит ${economic[Terrain.UNIT1]!!.price}  монет," +
                    " отнимает от дохода ${economic[Terrain.UNIT1]!!.sale} \n монет," +
                    " защита ${economic[Terrain.UNIT1]!!.protection} единиц. ",
            x = (screenX*0.15).toInt(),
            y = (screenY*0.62).toInt(),
            size = size_text,
            bold = true
        )
        TextRender(
            textMeasurer,
            name = "    · Unit2 стоит ${economic[Terrain.UNIT2]!!.price} монет," +
                    " отнимает от дохода ${economic[Terrain.UNIT2]!!.sale} \n монет," +
                    " защита ${economic[Terrain.UNIT2]!!.protection} единиц. ",
            x = (screenX*0.15).toInt(),
            y = (screenY*0.72).toInt(),
            size = size_text,
            bold = true
        )

    }

    fun DrawScope.render_rules_3(textMeasurer: TextMeasurer){
        TextRender(
            textMeasurer,
            name = "5. Клетка вокруг юнитов и башен имеют защиту \n юнита или башни который их защищает.",
            x = (screenX*0.15).toInt(),
            y = (screenY*0.17).toInt(),
            size = size_text,
            bold = true
        )
        TextRender(
            textMeasurer,
            name = "6. Юнит может сходить на клетку вокруг себя, если защита этой клетки \n меньше, чем у юнита, а потом с нее продолжить.",
            x = (screenX*0.15).toInt(),
            y = (screenY*0.31).toInt(),
            size = size_text,
            bold = true
        )

        TextRender(
            textMeasurer,
            name = "7. Unit4 может ходить на клетку с любой защитой.",
            x = (screenX*0.15).toInt(),
            y = (screenY*0.48).toInt(),
            size = size_text,
            bold = true
        )

        TextRender(
            textMeasurer,
            name = "8. Юнит, ходя на клетку присваивает ее игроку.",
            x = (screenX*0.15).toInt(),
            y = (screenY*0.55).toInt(),
            size = size_text,
            bold = true
        )

        TextRender(
            textMeasurer,
            name = "9. Когда игрок начинает \n свой ход:",
            x = (screenX*0.15).toInt(),
            y = (screenY*0.62).toInt(),
            size = size_text,
            bold = true
        )

        TextRender(
            textMeasurer,
            name = "    · Доход прибавляется к казне.",
            x = (screenX*0.15).toInt(),
            y = (screenY*0.69).toInt(),
            size = size_text,
            bold = true
        )

        TextRender(
            textMeasurer,
            name = "    · Все юниты игрока снова могут ходить.",
            x = (screenX*0.15).toInt(),
            y = (screenY*0.76).toInt(),
            size = size_text,
            bold = true
        )

    }
    fun DrawScope.render_rules_2(textMeasurer: TextMeasurer){
        TextRender(
            textMeasurer,
            name = "    · Unit3 стоит ${economic[Terrain.UNIT3]!!.price} монет," +
                    " отнимает от дохода ${economic[Terrain.UNIT3]!!.sale} монет," +
                    " защита ${economic[Terrain.UNIT3]!!.protection} единиц. ",
            x = (screenX*0.15).toInt(),
            y = (screenY*0.17).toInt(),
            size = size_text,
            bold = true
        )
        TextRender(
            textMeasurer,
            name = "    · Unit4 стоит ${economic[Terrain.UNIT4]!!.price} монет," +
                    " отнимает от дохода ${economic[Terrain.UNIT4]!!.sale} монет," +
                    " защита ${economic[Terrain.UNIT4]!!.protection} единиц. ",
            x = (screenX*0.15).toInt(),
            y = (screenY*0.27).toInt(),
            size = size_text,
            bold = true
        )

        TextRender(
            textMeasurer,
            name = "4.2. Здания: ",
            x = (screenX*0.15).toInt(),
            y = (screenY*0.4).toInt(),
            size = size_text,
            bold = true
        )

        TextRender(
            textMeasurer,
            name = "    · Ферма стоит сначало ${economic[Terrain.immutableFARM]!!.price} монет," +
                    " но при каждой покупке цена \n увеличивается на 20 монет," +
                    " защита ${economic[Terrain.FARM]!!.protection} единиц.",
            x = (screenX*0.15).toInt(),
            y = (screenY*0.44).toInt(),
            size = size_text,
            bold = true
        )

        TextRender(
            textMeasurer,
            name = "    · Башня стоит ${economic[Terrain.TOWER]!!.price} монет," +
                    " отнимает от дохода ${economic[Terrain.TOWER]!!.income} \n монет, " +
                    "защита ${economic[Terrain.TOWER]!!.protection} единиц.",
            x = (screenX*0.15).toInt(),
            y = (screenY*0.6).toInt(),
            size = size_text,
            bold = true
        )

        TextRender(
            textMeasurer,
            name = "    · Улучшенная башня \n стоит ${economic[Terrain.HARD_TOWER]!!.price} монет," +
                    " отнимает от дохода ${economic[Terrain.HARD_TOWER]!!.income} монет, " +
                    "защита ${economic[Terrain.HARD_TOWER]!!.protection} единиц.",
            x = (screenX*0.15).toInt(),
            y = (screenY*0.73).toInt(),
            size = size_text,
            bold = true
        )


    }

    override fun onEnter() {

    }

    override fun onExit() {

    }

}