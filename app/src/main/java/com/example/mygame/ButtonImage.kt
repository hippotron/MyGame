package com.example.mygame


import android.graphics.BitmapFactory
import android.graphics.RectF
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.LocalContext

class ButtonImage(
    val x: Float,
    val y: Float,
    val dx: Float,
    val dy: Float,
    val name: Int

) {
    @Composable
    fun Render() {
        // эта строка получает доступ к контексту, он необходим для доступа к картинкам
        val context = LocalContext.current
        // загружает картинку
        val image = remember {
            /* BitmapFactory нужен для создания изображения
             decodeResource метод для загрузки картинки из папки res/drawble
             context.resources получает доступ к ресурсам
             name это переменная пути к картинке, в место него можно написать конкретный путь к картинке
            */
            BitmapFactory.decodeResource(context.resources, name)
        }
        // Canvas создает холст(область для рисования) на весь экран
        Canvas(
            modifier = Modifier
                .fillMaxSize()
        ) {
            /* создается прямоугольник который определяет где и какого размера будет нарисована картинка
             x это откуда рисовать по x
             y откуда рисовать по y
             x+dx а это до куда рисовать картинку по x
             y+dy это до куда рисовать картинку по y */

            val Rect = RectF(x, y, x + dx, y + dy)
            /* рисует картинку
             image картинка которую нужно нарисовать
             null означает что мы хотим нарисовать полное изброжение а не его часть
             Rect прямоугольник, который определяет область где будет нарисована картинка
             null означает что мы рисуем картинку без доп. эффектов(прозрачности и тд)*/
             drawContext.canvas.nativeCanvas.drawBitmap(image, null, Rect, null)
        }
        //}
    }

    fun click(mx: Float, my: Float): Boolean{
        /*
        Log.d("","x = $x")
        Log.d("","y = $y")
        //Log.d("","dx = $dx")
        //Log.d("","dy = $dy")
        Log.d("","x+dx = ${x+dx}")
        Log.d("","y+dy = ${x+dy}")
        Log.d("","mx = $mx")
        Log.d("","my = $mx")
        */

        if (x<mx && mx<x+dx && y<my && my<y+dy){
            //Log.d("","777")
            return true
        } else{
            return false
        }
    }
}